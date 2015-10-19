package com.github.bluzwong.learn_rx;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

/**
 * Created by wangzhijie on 2015/10/19.
 */
public class DynamicProxy {
    interface HelloWorld {
        void sayHelloWorld();
        void sayGoodBye();
    }

    @Retention(RetentionPolicy.RUNTIME)
    @Target(ElementType.METHOD)
    @interface MyMethod {
        String name();
    }

    static class HelloWorldHandler implements InvocationHandler {

        private Object target;

        public HelloWorldHandler(Object target) {
            this.target = target;
        }

        @Override
        public Object invoke(Object o, Method method, Object[] objects) throws Throwable {
            Object result;

            String methodName = method.getName();
            Method[] methods = target.getClass().getDeclaredMethods();
            for (Method thisMethod : methods) {
                MyMethod myMethod = thisMethod.getAnnotation(MyMethod.class);
                if (myMethod != null && methodName.equals(myMethod.name())) {
                    System.out.println(methodName + "() will invoke by annotation");
                    Object ret = thisMethod.invoke(target, objects);
                    System.out.println(methodName + "() did invoke by annotation\n");
                    return ret;
                }
            }
            Method sayHelloWorld = target.getClass().getDeclaredMethod(methodName, method.getParameterTypes());
            System.out.println(methodName + "() will invoke by method name");
            result = sayHelloWorld.invoke(target, objects);
            //result = method.invoke(instance, objects);
            System.out.println(methodName + "() did invoke by method name\n");
            return result;
        }
    }

    public static void main(String[] args) {
        HelloWorldHandler handler = new HelloWorldHandler(new Object() {
            @MyMethod(name = "sayGoodBye")
            public void sayHelloWorld() {
                System.out.println("lol, it works, say hello world invoking #########");
            }

            @MyMethod(name = "sayHelloWorld")
            public void sayGoodBye() {
                System.out.println("say good bye invoking @@@@@@@@ ");
            }

        });
        Object obj = Proxy.newProxyInstance(HelloWorld.class.getClassLoader(),
                new Class[]{HelloWorld.class}, handler);
        HelloWorld helloWorld = (HelloWorld) obj;
        helloWorld.sayHelloWorld();
        helloWorld.sayGoodBye();
    }
}
