package com.plenituz.same.util;

import java.io.Serializable;

/**
 * Created by Plenituz on 16/11/2015 for Same.
 */
public class ToppedArrayMap<E, T> implements Serializable {
    Object[] keys;
    Object[][] values;

    public ToppedArrayMap(int valueCapacity) {
        keys = new Object[13];
        values = new Object[13][valueCapacity];
    }

    public void add(E key, T values) {
        if(keys[keys.length-1] != null){//if the last case is used
            expandKeys();//then expand the array
        }
        int next;
        for(next = 0; next < keys.length; next++){//find out the next empty case
            if(keys[next] == null)
                break;
        }
        addValues((E) keys[next], values, next);
        //finally add stuff to the arrays
        keys[next] = key;
    }

    private void addValues(E key, T values, int loc){
        int next = loc;
        //int next = this.values.length;

        if(this.values[next][this.values[next].length-1] == null){
            //if the last case is free, find the appropriate case to add to
            for(int i = 0; i < this.values[next].length; i++){
                if(this.values[next][i] == null){
                    this.values[next][i] = values;
                    break;
                }
            }
        }else{
            //if the last case is used it means the array is full, push everything down and add the value
            System.arraycopy(this.values[next], 1, this.values[next], 0, this.values[next].length - 1);
            /*for(int i = 0; i < this.values[next].length-1; i++){
                this.values[next][i] = this.values[next][i+1];
            }*/
            this.values[next][this.values[next].length-1] = values;
        }
    }

    public void addValues(E key, T values){
        int next = getLocation(key);
        //int next = this.values.length;

        if(this.values[next][this.values[next].length-1] == null){
            //if the last case is free, find the appropriate case to add to
            for(int i = 0; i < this.values[next].length; i++){
                if(this.values[next][i] == null){
                    this.values[next][i] = values;
                    break;
                }
            }
        }else{
            //if the last case is used it means the array is full, push everything down and add the value
            System.arraycopy(this.values[next], 1, this.values[next], 0, this.values[next].length - 1);
            /*for(int i = 0; i < this.values[next].length-1; i++){
                this.values[next][i] = this.values[next][i+1];
            }*/
            this.values[next][this.values[next].length-1] = values;
        }
    }

    private void expandKeys() {
        Object[] keyBuffer = keys;
        Object[][] valueBuffer = values;

        keys = new Object[keyBuffer.length*2];
        values = new Object[keyBuffer.length*2][];

        for(int i = 0; i < keyBuffer.length; i++){
            keys[i] = keyBuffer[i];
            values[i] = new Object[valueBuffer[i].length];
            System.arraycopy(valueBuffer[i], 0, values[i], 0, valueBuffer[i].length);
            /*for(int j = 0; j < valueBuffer[i].length; j++){
                values[i][j] = valueBuffer[i][j];
            }*/
        }
    }

    public void reset(int valueCapacity) {
        keys = new Object[13];
        values = new Object[13][valueCapacity];
    }

    public E remove(int location) {
        E r = (E) keys[location];
        keys[location] = null;
        values[location] = null;
        for(int i = location; i < keys.length-1; i++){
            keys[i] = keys[i+1];
            values[i] = values[i+1];
            if(i != location && keys[i] == null)
                break;
        }
        keys[keys.length-1] = null;
        values[values.length-1] = null;
        return r;
    }

    /**
     * if there is twice the same object in the array only the first one will be removed
     * @param key key
     * @return true if removed
     */
    public boolean remove(E key){
        for(int i = 0; i < keys.length; i++){
            if(keys[i] == key){
                remove(i);
                return true;
            }
        }
        return false;
    }

    public E get(int location) {
        return (E) keys[location];
    }

    public Object[] get(E key){
        int l;
        if((l = getLocation(key)) != -1){
            return values[l];
        }

        else
            return null;
    }

    public T get(E key, int valueIndex){
        return get(getLocation(key), valueIndex);
    }

    public T get(int keyIndex, int valueIndex){
        return (T) values[keyIndex][valueIndex];
    }

    /**
     *
     * @param key
     * @return -1 if not found
     */
    public int getLocation(E key){
        for(int i = 0; i < keys.length; i++){
            if(keys[i] == key)
                return i;
        }
        return -1;
    }



    public int size() {
        for (int i = 0; i < keys.length; i++){
            if(keys[i] == null)
                return i-1;
        }
        return keys.length;
    }
}
