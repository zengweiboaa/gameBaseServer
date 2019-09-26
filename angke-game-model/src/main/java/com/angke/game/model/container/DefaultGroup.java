package com.angke.game.model.container;

import java.util.AbstractSet;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.atomic.AtomicInteger;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.angke.game.model.GlobalId;
import com.angke.game.model.Model;

import io.netty.util.concurrent.EventExecutor;
import io.netty.util.internal.PlatformDependent;

public class DefaultGroup<R extends Model> extends AbstractSet<R> implements Group<R> {

	protected Logger logger = LoggerFactory.getLogger(DefaultGroup.class);
	private static final AtomicInteger nextId = new AtomicInteger();
    private final String name;
    private final EventExecutor executor;
    private final ConcurrentMap<GlobalId, R> objects = PlatformDependent.newConcurrentHashMap();
	
    public DefaultGroup(String name, EventExecutor executor) {
        if (name == null) {
            throw new NullPointerException("name");
        }
        this.name = name;
        this.executor = executor;
    }
    
	@Override
	public String name() {
		return name;
	}

	@Override
	public R find(GlobalId id) {
		return objects.get(id);
	}

	@Override
	public ConcurrentMap<GlobalId, R> find(Class<? extends R> clz) {
		ConcurrentMap<GlobalId, R> objs = PlatformDependent.newConcurrentHashMap();
		for (R v : objects.values()) {
			if (clz.equals(v.getClass())) {
				objs.put(v.id(), v);
			}
		}
		return objs;
	}
	
	@Override
	public int size() {
		return objects.size();
	}

	@Override
	public int size(Class<? extends R> clz) {
		int i = 0;
		for (R v : objects.values()) {
			if (clz.equals(v.getClass())) {
				i++;
			}
		}
		return i;
	}

	@Override
	public Iterator<R> iterator() {
		return (Iterator<R>) new CombinedIterator<R>(
				objects.values().iterator());
	}

	@Override
	public int compareTo(Group<R> o) {
		int v = name().compareTo(o.name());
        if (v != 0) {
            return v;
        }

        return System.identityHashCode(this) - System.identityHashCode(o);
	}
	
	@Override
	public Object[] toArray() {
		Collection<R> gameRoomAll = new ArrayList<R>(size());
		gameRoomAll.addAll(objects.values());
        return gameRoomAll.toArray();
	}

	@Override
	public <T> T[] toArray(T[] a) {
		Collection<R> gameRoomAll = new ArrayList<R>(size());
		gameRoomAll.addAll(objects.values());
        return gameRoomAll.toArray(a);
	}
	
	@Override
	public boolean add(R obj) {
		
		ConcurrentMap<GlobalId, R> map = objects;

        boolean added = map.putIfAbsent(obj.id(), obj) == null;
        
        if (added) {
        	return added;
        }

		return false;
	}

	@SuppressWarnings("unchecked")
	@Override
	public boolean remove(Object o) {
		R g = null;
        if (o instanceof GlobalId) {
            g = objects.remove(o);
            if (g == null) {
                g = objects.remove(o);
            }
        } else if (o instanceof Model) {
            g = (R) o;
            g = objects.remove(g.id());
        }

        if (g == null) {
            return false;
        }

//        c.closeFuture().removeListener(remover);
        return true;
	}
	
	/**
	 * 容器是否为空
	 */
	@Override
	public boolean isEmpty() {
		return objects.isEmpty();
	}
}
