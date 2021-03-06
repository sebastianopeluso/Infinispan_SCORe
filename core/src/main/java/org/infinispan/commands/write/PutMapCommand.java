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
package org.infinispan.commands.write;

import org.infinispan.commands.Visitor;
import org.infinispan.container.entries.MVCCEntry;
import org.infinispan.context.Flag;
import org.infinispan.context.InvocationContext;
import org.infinispan.notifications.cachelistener.CacheNotifier;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

/**
 * @author Mircea.Markus@jboss.com
 * @since 4.0
 */
public class PutMapCommand implements WriteCommand {
    public static final byte COMMAND_ID = 9;

    Map<Object, Object> map;
    Map<Object, Object> writeSkewValue;
    CacheNotifier notifier;
    long lifespanMillis = -1;
    long maxIdleTimeMillis = -1;
    Set<Flag> flags;

    public PutMapCommand() {
        writeSkewValue = new HashMap<Object, Object>();
    }

    public PutMapCommand(Map map, CacheNotifier notifier, long lifespanMillis, long maxIdleTimeMillis, Set<Flag> flags) {
        super();
        this.map = map;
        this.notifier = notifier;
        this.lifespanMillis = lifespanMillis;
        this.maxIdleTimeMillis = maxIdleTimeMillis;
        this.flags = flags;
    }

    public void init(CacheNotifier notifier) {
        this.notifier = notifier;
    }

    public Object acceptVisitor(InvocationContext ctx, Visitor visitor) throws Throwable {
        return visitor.visitPutMapCommand(ctx, this);
    }

    private MVCCEntry lookupMvccEntry(InvocationContext ctx, Object key) {
        return (MVCCEntry) ctx.lookupEntry(key);
    }

    public Object perform(InvocationContext ctx) throws Throwable {
        for (Entry<Object, Object> e : map.entrySet()) {
            Object key = e.getKey();
            MVCCEntry me = lookupMvccEntry(ctx, key);
            notifier.notifyCacheEntryModified(key, me.getValue(), true, ctx);
            if(me.isRemoteWriteSkewNeeded() && !writeSkewValue.containsKey(key)) {
                writeSkewValue.put(key, me.getValue());
            }
            me.setValue(e.getValue());
            me.setLifespan(lifespanMillis);
            me.setMaxIdle(maxIdleTimeMillis);
            notifier.notifyCacheEntryModified(key, me.getValue(), false, ctx);
        }
        return null;
    }

    public Map<Object, Object> getMap() {
        return map;
    }

    public void setMap(Map<Object, Object> map) {
        this.map = map;
    }

    public byte getCommandId() {
        return COMMAND_ID;
    }

    public Object[] getParameters() {
        return new Object[]{map, writeSkewValue, lifespanMillis, maxIdleTimeMillis, flags};
    }

    public void setParameters(int commandId, Object[] parameters) {
        map = (Map) parameters[0];
        writeSkewValue = (Map) parameters[1];
        lifespanMillis = (Long) parameters[2];
        maxIdleTimeMillis = (Long) parameters[3];
        if (parameters.length>4) {
            this.flags = (Set<Flag>) parameters[4];
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        PutMapCommand that = (PutMapCommand) o;

        if (lifespanMillis != that.lifespanMillis) return false;
        if (maxIdleTimeMillis != that.maxIdleTimeMillis) return false;
        if (map != null ? !map.equals(that.map) : that.map != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = map != null ? map.hashCode() : 0;
        result = 31 * result + (int) (lifespanMillis ^ (lifespanMillis >>> 32));
        result = 31 * result + (int) (maxIdleTimeMillis ^ (maxIdleTimeMillis >>> 32));
        return result;
    }

    @Override
    public String toString() {
        return new StringBuilder()
                .append("PutMapCommand{map=")
                .append(map)
                .append(", flags=").append(flags)
                .append(", lifespanMillis=").append(lifespanMillis)
                .append(", maxIdleTimeMillis=").append(maxIdleTimeMillis)
                .append("}")
                .toString();
    }

    public boolean shouldInvoke(InvocationContext ctx) {
        return true;
    }

    public boolean isSuccessful() {
        return true;
    }

    public boolean isConditional() {
        return false;
    }

    public Set<Object> getAffectedKeys() {
        return map.keySet();
    }

    public long getLifespanMillis() {
        return lifespanMillis;
    }

    public long getMaxIdleTimeMillis() {
        return maxIdleTimeMillis;
    }

    @Override
    public Set<Flag> getFlags() {
        return flags;
    }

    @Override
    public void setFlags(Set<Flag> flags) {
        this.flags = flags;
    }

    @Override
    public Map<Object, Object> getKeyAndValuesForWriteSkewCheck() {
        return writeSkewValue;
    }
}
