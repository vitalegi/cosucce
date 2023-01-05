package it.vitalegi.budget.resource;

import java.security.Principal;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/app")
public class UserResource {

	@GetMapping(path = "/test")
	public String test(Principal principal) {
		return principal.getName();
	}

}
