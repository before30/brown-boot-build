package cc.before30.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * User: before30 
 * Date: 2016. 12. 14.
 * Time: 오후 12:16
 */

@Controller
public class HelloController {

	@RequestMapping("/")
	public String index(Model model) {
		model.addAttribute("name", "world");
		return "hello";
	}

	@RequestMapping("/{name}")
	public String index(Model model, @PathVariable String name) {
		model.addAttribute("name", name);
		return "hello";
	}
}
