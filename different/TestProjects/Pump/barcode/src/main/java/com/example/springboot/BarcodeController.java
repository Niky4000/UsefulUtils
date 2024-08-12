package com.example.springboot;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import javax.servlet.http.HttpServletResponse;
import org.apache.tomcat.util.http.fileupload.IOUtils;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import ru.ibs.pmp.monitoring.utils.BarCodeUtils;

@RestController
public class BarcodeController {

	@RequestMapping(value = "/{text}", method = RequestMethod.GET)
	public void getImageAsByteArray(HttpServletResponse response, @PathVariable("text") String text) throws IOException {
		try (InputStream in = new ByteArrayInputStream(BarCodeUtils.generate(text))) {
			response.setContentType(MediaType.IMAGE_JPEG_VALUE);
			IOUtils.copy(in, response.getOutputStream());
		}
	}
}
