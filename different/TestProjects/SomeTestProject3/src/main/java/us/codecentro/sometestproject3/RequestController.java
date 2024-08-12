package us.codecentro.sometestproject3;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
//@RequestMapping("/")
public class RequestController {

	
	// http://codenco.us/very-long-string-fkfdonsdjkbfkjsdbkdsnbfkdsnbfksdbfdskbdekjblnfd ->
	// <html><head></head><body><h1>Invoice Page</h1></body></html>
	
	// http://localhost:8080/jnsnljf-32332-88484
	
	@GetMapping("/")
	@ResponseBody
	public String shortenUrlMethod(@RequestParam("str") String message) {
		System.out.println(message); //MTOM
//		HTTP3
// 302
		return "<html><head></head><body><h1>Invoice Page</h1></body></html>";
	}
}
