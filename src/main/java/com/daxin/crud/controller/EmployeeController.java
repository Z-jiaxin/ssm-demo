package com.daxin.crud.controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.daxin.crud.bean.Employee;
import com.daxin.crud.bean.Msg;
import com.daxin.crud.service.EmployeeService;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;

/**
 * 处理员工CRUD请求
 * @author W10
 *
 */

@Controller
public class EmployeeController {

	@Autowired
	EmployeeService employeeService;
	
	/**
	 * 单个批量二合一
	 * 批量删除：1-2-3
	 * 单个删除：1
	 * @param id
	 * @return
	 */
	@ResponseBody
	@RequestMapping(value="/emp/{ids}",method=RequestMethod.DELETE)
	public Msg deleteEmp(@PathVariable("ids")String ids) {
		if(ids.contains("-")) {
			List<Integer> del_ids = new ArrayList<>();
			String[] str_ids = ids.split("-");
			//组装id的集合
			for(String string : str_ids) {
				del_ids.add(Integer.parseInt(string));
			}
			employeeService.deleteBatch(del_ids);
		}else {
			Integer id = Integer.parseInt(ids);
			employeeService.deleteEmp(id);
		}
		return Msg.success();
	}
	
	@ResponseBody
	@RequestMapping(value="/emps/{id}",method=RequestMethod.DELETE)
	public Msg deleteEmpById(@PathVariable("id")Integer id) {
		employeeService.deleteEmp(id);
		return Msg.success();
	}
	
	@ResponseBody
	@RequestMapping(value ="/emps/{empId}",method=RequestMethod.PUT)
	public Msg saveEmp(Employee employee) {
		//System.out.println(employee);
		employeeService.updateEmp(employee);
		return Msg.success();
	}
	
	/**
	 * 根据id查询员工
	 * @param id
	 * @return
	 */
	@RequestMapping(value="/emps/{id}",method=RequestMethod.GET)
	@ResponseBody
	public Msg getEmp(@PathVariable("id")Integer id) {
		Employee employee = employeeService.getEmp(id);
		return Msg.success().add("emp",employee);
	}
	
	/**
	 * 检查用户名是否可用
	 * @param empName
	 * @return
	 */
	@ResponseBody
	@RequestMapping("/checkuser")
	public Msg checkuse(@RequestParam("empName")String empName) {
		//先判断用户名是否是合法的表达式
		String regx = "(^[a-zA-Z0-9_-]{6,16}$)|(^[\\u2E80-\\u9FFF]{2,5})";
		if(!empName.matches(regx)) {
			return Msg.fail().add("va_msg", "用户名可以是2-5位中文或者6-12位英文和数字的组合");
		}
		//数据库用户重复校验
		boolean b = employeeService.checkUser(empName);
		if(b) {
			return Msg.success();
		}else {
			return Msg.fail().add("va_msg", "用户名不可用");
		}
	}
	/**
	 * 员工保存
	 * 1、支持JSR303校验
	 * 2、导入Hibernate-Validator
	 * @return
	 */
	@RequestMapping(value="/emps",method=RequestMethod.POST)
	@ResponseBody
	public Msg saveEmp(@Valid Employee employee,BindingResult result) {
		if(result.hasErrors()) {
			//校验失败，应该返回失败，在模态框中显示校验失败的错误信息
			Map<String,Object> map = new HashMap<>();
			List<FieldError> errors = result.getFieldErrors();
			for(FieldError fieldError:errors) {
				System.out.println("错误的字段名："+fieldError.getField());
				System.out.println("错误信息："+fieldError.getDefaultMessage());
				map.put(fieldError.getField(), fieldError.getDefaultMessage());
			}
			return Msg.fail().add("errorFields", map);
		}else {
			employeeService.saveEmp(employee);
			return Msg.success();
		}
	}
	/**
	 * 导入jackson包
	 * @param pn
	 * @return
	 */
	@RequestMapping("/emps")
	@ResponseBody
	public Msg getEmpsWithJson(@RequestParam(value="pn",defaultValue = "1")Integer pn) {
		//引入PageHelper分页插件
		//在查询之前只需要调用,，传入页码，以及每页的大小
		PageHelper.startPage(pn, 5);
		//startPage后面紧跟的这个查询就是一个分页查询
		List<Employee> emps = employeeService.getAll();
		//使用pageInfo包装查询后的结果，只需要将pageInfo交给页面就行了
		//封装了详细的分页信息，包括我们查询出来的数据
		PageInfo page = new PageInfo(emps,5);
		return Msg.success().add("pageInfo",page);
	}
	/**
	 * 查询员工数据（分页查询）
	 * @return
	 */
	//@RequestMapping("/emps")
	public String getEmps(@RequestParam(value="pn",defaultValue = "1")Integer pn,Model model) {
		//引入PageHelper分页插件
		//在查询之前只需要调用,，传入页码，以及每页的大小
		PageHelper.startPage(pn, 5);
		//startPage后面紧跟的这个查询就是一个分页查询
		List<Employee> emps = employeeService.getAll();
		//使用pageInfo包装查询后的结果，只需要将pageInfo交给页面就行了
		//封装了详细的分页信息，包括我们查询出来的数据
		PageInfo page = new PageInfo(emps,5);
		model.addAttribute("pageInfo",page);
		return "list";
	}
}
