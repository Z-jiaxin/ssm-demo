package com.daxin.crud.test;

import java.util.UUID;

import javax.annotation.security.RunAs;

import org.apache.ibatis.session.SqlSession;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.daxin.crud.bean.Department;
import com.daxin.crud.bean.DepartmentExample;
import com.daxin.crud.bean.Employee;
import com.daxin.crud.dao.DepartmentMapper;
import com.daxin.crud.dao.EmployeeMapper;

/**
 * 测试dao层的工作
 * @author W10
 * 推荐Spring的项目就可以使用Spring的单元测试，可以自动注入我们需要的组件
 * 1、导入SpringTest模块
 * 2、@ContextConfiguration指定Spring配置文件的位置
 * 3、直接autowired要使用的组件即可
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"classpath:applicationContext.xml"})
public class MapperTest {
	
	@Autowired
	DepartmentMapper departmentMapper;
	
	@Autowired
	EmployeeMapper employeeMapper;
	
	@Autowired
	SqlSession sqlSession;
	
	/**
	 * 测试DepartmentMapper
	 */
	@Test
	public void testCRUD() {
		/*
		 * //1、创建Springioc容器 ApplicationContext ioc = new
		 * ClassPathXmlApplicationContext("applicationContext.xml"); 
		 * //2、从容器中获取mapper
		 * ioc.getBean(DepartmentMapper);
		 */
		System.out.println(departmentMapper);
		
		//1、插入几个部门
//		departmentMapper.insertSelective(new Department(null,"开发部"));
//		departmentMapper.insertSelective(new Department(null,"测试部"));
		
		//2、生成员工数据，测试员工插入
//		employeeMapper.insertSelective(new Employee(null,"Jerry","M","Jerry@daxin.com",1));
		
		//3、批量插入多个员工，批量，使用可以执行批量操作的sqlSession
		
		/*
		 * for() { employeeMapper.insertSelective(new
		 * Employee(null,"Jerry","M","Jerry@daxin.com",1)); }
		 */
		EmployeeMapper mapper = sqlSession.getMapper(EmployeeMapper.class);   
		for(int i = 0; i<100; i++) {
			String uid = UUID.randomUUID().toString().substring(0,5)+i;
			mapper.insertSelective(new Employee(null,uid,"M",uid+"@daxin.com",1));
			System.out.println("批量完成");
		}
		
	}
}
