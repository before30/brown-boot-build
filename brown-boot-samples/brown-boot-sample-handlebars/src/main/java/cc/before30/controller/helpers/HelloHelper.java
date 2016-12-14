package cc.before30.controller.helpers;

import cc.before30.boot.starters.handlebars.autoconfigure.HandlebarsHelper;

import java.util.Optional;

/**
 * User: before30 
 * Date: 2016. 12. 14.
 * Time: 오후 12:17
 */

@HandlebarsHelper
public class HelloHelper {

	public String sayHello(String name) {
		return String.format("Hello %s!!!", Optional.ofNullable(name).orElseGet(() -> "unknown"));
	}

}
