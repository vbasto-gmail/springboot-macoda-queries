package net.macoda.queries;

import java.io.IOException;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class WelcomeController {

	@GetMapping("/welcome")
	public String welcome(@RequestParam(value="query", defaultValue="O valor default é olá...!") String query) {
		return "GET: Welcome to springboot heroku macoda queries: " + query;
	}
	
    @PostMapping("/welcome")
    // Com @RequestBody funciona bem se o POST for "raw"
    // Com @RequestParam funciona bem se o POST for "form-data"    
    //public String run(@RequestBody String query) throws IOException {
    public String run(@RequestParam(value="query", defaultValue="O valor default é olá...!") String query) throws IOException {
		return "POST: Welcome to springboot heroku macoda queries: " + query;
    }	
}
