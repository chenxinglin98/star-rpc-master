package com.star.serializer.KryoSerializer;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;
import com.star.exception.SerializeException;
import com.star.remoting.entity.RpcRequest;
import com.star.remoting.entity.RpcResponse;
import com.star.serializer.Serializer;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

/**
 * @description:
 * @author: 陈星霖
 * @date: 2023-01-04 23:44
 */
public class KryoSerializer implements Serializer {
    //Kryo是线程不安全的,使用ThreadLocal确保线程安全
    //1.因为每一个对象都要使用Kryo所以考虑让他成为类变量
    //2.所以引出了线程安全,多线程访问的是一个Kryo对象,而会同时调用一个Kryo进行操作
    //3.所以要确保线程安全
    //4.一个threadLocal相当于一个变量(线程独享的)
    //5.每个线程都有个threadlocalMap里面存的<threadlocal,object>
    //其实就是首先你是个类变量,然后为了使得这个变量线程安全使用ThreadLocal
    //static的是这个ThreadLocal,ThreadLocalMap在thread里不是static的
    //如上文所述，ThreadLocal 适用于如下两种场景
    //1、每个线程需要有自己单独的实例
    //2、实例需要在多个方法中共享，但不希望被多线程共享
    //这个线程启动了,然后会在threadLocalMap里面存这个kryoThreadLocal
    //每个线程的kryoThreadLocal也就是其实是一个Kryo是互相隔离的,而使用这些类方法,也都是用的他们自己的Kryo
    private static final ThreadLocal<Kryo> kryoThreadLocal = ThreadLocal.withInitial(() -> {
        Kryo kryo = new Kryo();
        kryo.register(RpcRequest.class);
        kryo.register(RpcResponse.class);
        return kryo;
    });
    public byte[] serialize(Object obj) {
        try(ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            Output output = new Output(byteArrayOutputStream);)
        {
            Kryo kryo = kryoThreadLocal.get();
            kryo.writeObject(output,obj);
            //remove了不就没有在这个线程中共享了么?,序列化完了,就不要了,下次序列号下次还会再创建新的kryo
            //如果不remove得话,对于netty可能线程是从线程池里面取的,仍然会有之前的Kryo,导致内存一直得不到释放
            kryoThreadLocal.remove();
            return output.toBytes();
        } catch (IOException e) {
            throw new SerializeException("Serialization failed");
        }
    }

    @Override
    public <T> T deserialize(byte[] bytes, Class<T> clazz) {
        try(ByteArrayInputStream byteArrayInputStream =new ByteArrayInputStream(bytes);
            Input input = new Input(byteArrayInputStream);
        ) {
            Kryo kryo = kryoThreadLocal.get();
            T t = kryo.readObject(input, clazz);
            //remove了不就没有在这个线程中共享了么?
            kryoThreadLocal.remove();
            return t;
        } catch (IOException e) {
            throw new SerializeException("Deserialization failed");
        }
    }

}
