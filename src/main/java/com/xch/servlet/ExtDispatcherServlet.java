package com.xch.servlet;

import com.xch.annonation.ExtController;
import com.xch.annonation.ExtRequestMapping;
import com.xch.utils.ClassUtil;
import org.apache.commons.lang.StringUtils;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 *
 * 自定义前端控制器
 * @author xiech
 * @create 2020-01-19 14:52
 */
public class ExtDispatcherServlet extends HttpServlet {

    //springmvc bean容器
    private ConcurrentHashMap<String,Object> springmvcBeans=new ConcurrentHashMap<>();
    //handlerMapping 容器
    private ConcurrentHashMap<String,Object> urlBeans=new ConcurrentHashMap<>();
    //url 方法容器
    private ConcurrentHashMap<String,String> urlMethodBeans=new ConcurrentHashMap<>();

    @Override
    public void init() throws ServletException {
        List<Class<?>> classes = ClassUtil.getClasses("com.xch.controller");
        try {
            //扫包将所有带@ExtController注解的类放入springmvc容器
            initSpringmvcBeans(classes);
            //将url映射与方法关联起来
            initUrlBeans();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InstantiationException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
       doPost(req,resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String uri = req.getRequestURI();
        //根据url获取对象实例
        Object object = urlBeans.get(uri);
        if(object==null){
            resp.getWriter().print("url not found 404");
            return;
        }
        //根据url获取对象方法名称
        String methodName=urlMethodBeans.get(uri);
        if(StringUtils.isBlank(methodName)){
            resp.getWriter().print("method not found ");
            return;
        }
        //反射执行方法
        try {
            Method method = object.getClass().getMethod(methodName);
            String pageName = (String) method.invoke(object);
            extResourceViewResolver(pageName,req,resp);
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }


    }

    private void extResourceViewResolver(String pageName, HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String prefix="/";
        String suffix=".jsp";
        req.getRequestDispatcher(prefix+pageName+suffix).forward(req,resp);
    }

    //将url映射与方法关联起来
    private void initUrlBeans() throws IllegalAccessException, InstantiationException {
        for (Map.Entry<String, Object> entry : springmvcBeans.entrySet()) {
            //判断类上是否含有@ExtRequestMapping注解
            Object object = entry.getValue();

            String baseUrl="";
            ExtRequestMapping extRequestMapping = object.getClass().getAnnotation(ExtRequestMapping.class);
            if(extRequestMapping!=null){
                baseUrl+=extRequestMapping.value();
            }
            //获取类上所有的方法
            Method[] methods = object.getClass().getDeclaredMethods();
            //循环遍历判断方法上是否含有@ExtRequestMapping注解
            for (Method method : methods) {
                ExtRequestMapping methodDeclaredAnnotation = method.getAnnotation(ExtRequestMapping.class);
                if(methodDeclaredAnnotation!=null){
                    String methodUrl=baseUrl+methodDeclaredAnnotation.value();
                    urlBeans.put(methodUrl,object);
                    urlMethodBeans.put(methodUrl,method.getName());
                }
            }
        }


    }

    //扫包将所有带@ExtController注解的类放入springmvc容器
    private void initSpringmvcBeans(List<Class<?>> classes ) throws IllegalAccessException, InstantiationException {

        for (Class<?> clazz : classes) {
            //判断该类是否含有注解
            ExtController extController = clazz.getAnnotation(ExtController.class);
            if(extController!=null){
                String beanId=ClassUtil.toLowerCaseFirstOne(clazz.getSimpleName());
                //利用反射对象初始化
                Object o = clazz.newInstance();
                springmvcBeans.put(beanId,o);
            }
        }
    }
}
