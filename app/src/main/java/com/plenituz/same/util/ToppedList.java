package com.plenituz.same.util;

import java.io.Serializable;
import java.util.AbstractList;

/**
 * Created by Plenituz on 12/02/2015.
 */
public class ToppedList<E> extends AbstractList<E> implements Serializable{
    Object[] list;

    public ToppedList(int size) {
        list = new Object[size];
    }

    @Override
    public void add(int location, E object) {
        if(location >= size())
            throw new IndexOutOfBoundsException("location > size");
        else
            list[location] = object;
    }

    /**
     * if the list is full objects get added to the top and the first added (number 0) get removed
     * @param object
     * @return true is list is topped-off false otherwise
     */
    @Override
    public boolean add(E object) {
        for(int i = 0; i < list.length; i++){
            Object o = list[i];
            if(o == null){
                list[i] = object;
                return false;
            }
        }
        for(int i = 1; i < list.length; i++){
            list[i-1] = list[i];
        }
        list[list.length-1] = object;
        return true;
    }

    @Override
    public void clear() {
        list = new Object[list.length];
    }

    @Override
    public boolean contains(Object object) {
        for(Object o: list)
            if(o == object)
                return true;
        return false;
    }

    @Override
    public boolean isEmpty() {
        for(Object o: list)
            if(o != null)
                return false;
        return true;
    }

    @Override
    public boolean remove(Object object) {
        boolean hasRemoved = false;
        for(int i = 0; i < list.length; i++){
            if(list[i] == object){
                list[i] = null;
                hasRemoved = true;
            }
        }
        return hasRemoved;
    }

    @Override
    public int size() {
        return list.length;
    }

    @Override
    public Object[] toArray() {
        return list;
    }

    @Override
    public E get(int location) {
        if(location > size())
            throw new IndexOutOfBoundsException("location > size");
        else
            return (E) list[location];
    }

    @Override
    public E remove(int location) {
        E r;
        if(location > size())
            throw new IndexOutOfBoundsException("location > size");
        else {
            r = (E) list[location];
            list[location] = null;
        }
        return r;
    }
}
