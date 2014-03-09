/*
 * JBoss, Home of Professional Open Source
 * Copyright 2009 Red Hat Inc. and/or its affiliates and other
 * contributors as indicated by the @author tags. All rights reserved.
 * See the copyright.txt in the distribution for a full listing of
 * individual contributors.
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
 */
package org.infinispan.distribution;

import org.infinispan.Cache;
import org.infinispan.commands.write.ClearCommand;
import org.infinispan.commands.write.PutKeyValueCommand;
import org.infinispan.commands.write.RemoveCommand;
import org.infinispan.commands.write.ReplaceCommand;
import org.infinispan.remoting.transport.Address;
import org.infinispan.test.TestingUtil;
import org.infinispan.util.ObjectDuplicator;
import org.testng.annotations.Test;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;

@Test(groups = "functional", testName = "distribution.DistSyncFuncTest")
public class DistSyncFuncTest extends BaseDistFunctionalTest {

   public DistSyncFuncTest() {
      sync = true;
      tx = false;
      testRetVals = true;
   }

   public void testLocationConsensus() {
      String[] keys = new String[100];
      Random r = new Random();
      for (int i = 0; i < 100; i++) keys[i] = Integer.toHexString(r.nextInt());

      // always expect key to be mapped to adjacent nodes!
      for (String key : keys) {

         List<Address> owners = new ArrayList<Address>();
         for (Cache<Object, String> c : caches) {
            boolean isOwner = isOwner(c, key);
            if (isOwner) owners.add(addressOf(c));
            boolean secondCheck = getConsistentHash(c).locate(key, 2).contains(addressOf(c));
            assert isOwner == secondCheck : "Second check failed for key " + key + " on cache " + addressOf(c) + " isO = " + isOwner + " sC = " + secondCheck;
         }
         // check consensus
         assertOwnershipConsensus(key);
         assert owners.size() == 2 : "Expected 2 owners for key " + key + " but was " + owners;
      }
   }

   private void assertOwnershipConsensus(String key) {
      List l1 = getConsistentHash(c1).locate(key, 2);
      List l2 = getConsistentHash(c2).locate(key, 2);
      List l3 = getConsistentHash(c3).locate(key, 2);
      List l4 = getConsistentHash(c4).locate(key, 2);

      assert l1.equals(l2) : "L1 "+l1+" and L2 "+l2+" don't agree.";
      assert l2.equals(l3): "L2 "+l2+" and L3 "+l3+" don't agree.";
      assert l3.equals(l4): "L3 "+l3+" and L4 "+l4+" don't agree.";

   }

   public void testBasicDistribution() throws Throwable {
      for (Cache<Object, String> c : caches) assert c.isEmpty();

      getOwners("k1")[0].put("k1", "value");

      // No non-owners have requested the key, so no invalidations
      asyncWait("k1", PutKeyValueCommand.class);

      for (Cache<Object, String> c : caches) {
         if (isOwner(c, "k1")) {
            assertIsInContainerImmortal(c, "k1");
         } else {
            assertIsNotInL1(c, "k1");
         }
      }

      // should be available everywhere!
      assertOnAllCachesAndOwnership("k1", "value");

      // and should now be in L1

      for (Cache<Object, String> c : caches) {
         if (isOwner(c, "k1")) {
            assertIsInContainerImmortal(c, "k1");
         } else {
            assertIsInL1(c, "k1");
         }
      }
   }

   public void testPutFromNonOwner() {
      initAndTest();
      Cache<Object, String> nonOwner = getFirstNonOwner("k1");

      Object retval = nonOwner.put("k1", "value2");
      asyncWait("k1", PutKeyValueCommand.class, getSecondNonOwner("k1"));

      if (testRetVals) assert "value".equals(retval);
      assertOnAllCachesAndOwnership("k1", "value2");
   }

   public void testPutIfAbsentFromNonOwner() {
      initAndTest();
      Object retval = getFirstNonOwner("k1").putIfAbsent("k1", "value2");

      if (testRetVals) assert "value".equals(retval);

      assertOnAllCachesAndOwnership("k1", "value");

      c1.clear();
      asyncWait(null, ClearCommand.class);

      retval = getFirstNonOwner("k1").putIfAbsent("k1", "value2");
      asyncWait("k1", PutKeyValueCommand.class, getSecondNonOwner("k1"));
      if (testRetVals) assert null == retval;

      assertOnAllCachesAndOwnership("k1", "value2");
   }

   public void testRemoveFromNonOwner() {
      initAndTest();
      Object retval = getFirstNonOwner("k1").remove("k1");
      asyncWait("k1", RemoveCommand.class, getSecondNonOwner("k1"));
      if (testRetVals) assert "value".equals(retval);

      assertOnAllCachesAndOwnership("k1", null);
   }

   public void testConditionalRemoveFromNonOwner() {
      initAndTest();
      boolean retval = getFirstNonOwner("k1").remove("k1", "value2");
      if (testRetVals) assert !retval : "Should not have removed entry";

      assertOnAllCachesAndOwnership("k1", "value");

      assert caches.get(1).get("k1").equals("value");

      Cache<Object, String> owner = getFirstNonOwner("k1");

      retval = owner.remove("k1", "value");
      asyncWait("k1", RemoveCommand.class, getSecondNonOwner("k1"));
      if (testRetVals) assert retval : "Should have removed entry";

      assert caches.get(1).get("k1") == null : "expected null but received " + caches.get(1).get("k1");
      assertOnAllCachesAndOwnership("k1", null);
   }

   public void testReplaceFromNonOwner() {
      initAndTest();
      Object retval = getFirstNonOwner("k1").replace("k1", "value2");
      if (testRetVals) assert "value".equals(retval);

      asyncWait("k1", ReplaceCommand.class, getSecondNonOwner("k1"));

      assertOnAllCachesAndOwnership("k1", "value2");

      c1.clear();
      asyncWait(null, ClearCommand.class);

      retval = getFirstNonOwner("k1").replace("k1", "value2");
      if (testRetVals) assert retval == null;

      assertOnAllCachesAndOwnership("k1", null);
   }

   public void testConditionalReplaceFromNonOwner() {
      initAndTest();
      Cache<Object, String> nonOwner = getFirstNonOwner("k1");
      boolean retval = nonOwner.replace("k1", "valueX", "value2");
      if (testRetVals) assert !retval : "Should not have replaced";

      assertOnAllCachesAndOwnership("k1", "value");

      assert !nonOwner.getAdvancedCache().getComponentRegistry().getComponent(DistributionManager.class).getLocality("k1").isLocal();
      retval = nonOwner.replace("k1", "value", "value2");
      asyncWait("k1", ReplaceCommand.class, getSecondNonOwner("k1"));
      if (testRetVals) assert retval : "Should have replaced";

      assertOnAllCachesAndOwnership("k1", "value2");
   }

   public void testClear() {
      for (Cache<Object, String> c : caches) assert c.isEmpty();

      for (int i = 0; i < 10; i++) {
         getOwners("k" + i)[0].put("k" + i, "value" + i);
         // There will be no caches to invalidate as this is the first command of the test
         asyncWait("k" + i, PutKeyValueCommand.class);
      }

      // this will fill up L1 as well
      for (int i = 0; i < 10; i++) assertOnAllCachesAndOwnership("k" + i, "value" + i);

      for (Cache<Object, String> c : caches) assert !c.isEmpty();

      c1.clear();
      asyncWait(null, ClearCommand.class);

      for (Cache<Object, String> c : caches) assert c.isEmpty();
   }

   public void testKeyValueEntryCollections() {
      c1.put("1", "one");
      asyncWait("1", PutKeyValueCommand.class);
      c2.put("2", "two");
      asyncWait("2", PutKeyValueCommand.class);
      c3.put("3", "three");
      asyncWait("3", PutKeyValueCommand.class);
      c4.put("4", "four");
      asyncWait("4", PutKeyValueCommand.class);

      for (Cache c : caches) {
         Set expKeys = TestingUtil.getInternalKeys(c);
         Collection expValues = TestingUtil.getInternalValues(c);

         Set expKeyEntries = ObjectDuplicator.duplicateSet(expKeys);
         Collection expValueEntries = ObjectDuplicator.duplicateCollection(expValues);

         Set keys = c.keySet();
         for (Object key : keys) assert expKeys.remove(key);
         assert expKeys.isEmpty() : "Did not see keys " + expKeys + " in iterator!";

         Collection values = c.values();
         for (Object value : values) assert expValues.remove(value);
         assert expValues.isEmpty() : "Did not see keys " + expValues + " in iterator!";

         Set<Map.Entry> entries = c.entrySet();
         for (Map.Entry entry : entries) {
            assert expKeyEntries.remove(entry.getKey());
            assert expValueEntries.remove(entry.getValue());
         }
         assert expKeyEntries.isEmpty() : "Did not see keys " + expKeyEntries + " in iterator!";
         assert expValueEntries.isEmpty() : "Did not see keys " + expValueEntries + " in iterator!";
      }
   }
}
