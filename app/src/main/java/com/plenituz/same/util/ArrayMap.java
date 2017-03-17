package com.plenituz.same.util;

import java.io.Serializable;

/**
 * Created by Plenituz on 16/11/2015 for Same.
 */
public class ArrayMap<E, T> implements Serializable {
    Object[] keys;
    Object[][] values;

    public ArrayMap() {
        keys = new Object[13];
        values = new Object[13][5];
    }

    public void add(E key, T...values) {
        if(keys[keys.length-1] != null){//if the last case is used
            expandKeys();//then expand the array
        }
        int next;
        for(next = 0; next < keys.length; next++){//find out the next empty case
            if(keys[next] == null)
                break;
        }
        if(this.values[next].length < values.length){//check if the case can contain enought values
            expandForValue(next, values.length);//if not expand for this value only
        }
        //finally add stuff to the arrays
        keys[next] = key;
        System.arraycopy(values, 0, this.values[next], 0, values.length);
        /*for(int i = 0; i < values.length; i++){
            this.values[next][i] = values[i];
        }*/
    }

    public void addValues(E key, T...values){
        int loc = getLocation(key);
        int next = this.values.length;
        if(this.values[loc][this.values[loc].length-1] != null)
            for(int i = 0; i < this.values[loc].length; i++){
                if(this.values[loc][i] == null){
                    next = i;
                    if(this.values[loc].length > i + values.length)
                        break;
                    else
                        expandForValue(loc, this.values[loc].length + values.length);
                }
            }
        else{
            for(int i = 0; i < this.values[loc].length; i++)
                if(this.values[loc][i] == null){
                    next = i;
                    break;
                }
            expandForValue(loc, this.values[loc].length + values.length);
        }
        System.arraycopy(values, 0, this.values[loc], next, values.length);
        /*for(int i = 0; i < values.length; i++){
            this.values[loc][next + i] = values[i];
        }*/
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

    private void expandForValue(int value, int minimalSize){
        Object[] buffer  = values[value];
        values[value] = new Object[buffer.length*2 > minimalSize ? buffer.length*2 : minimalSize];
        System.arraycopy(buffer, 0, values[value], 0, buffer.length);
        /*for(int i = 0; i < buffer.length; i++){
            values[value][i] = buffer[i];
        }*/
    }

    public void clear() {
        keys = new Object[13];
        values = new Object[13][5];
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
        if((l = getLocation(key)) != -1)
            return  values[l];
        else
            return null;
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
                return i;
        }
        return keys.length;
    }
}
