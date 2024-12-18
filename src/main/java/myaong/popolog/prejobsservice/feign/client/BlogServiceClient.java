package myaong.popolog.prejobsservice.feign.client;

import myaong.popolog.prejobsservice.feign.dto.request.PrejobsRequest;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;

import java.util.List;

@FeignClient(name = "blog-service")
public interface BlogServiceClient {

	@PostMapping("/blog/profiles/prejobs")
	void createPrejobs(@RequestHeader(name = "memberId") Long memberId, @RequestBody List<PrejobsRequest> requests);
}
