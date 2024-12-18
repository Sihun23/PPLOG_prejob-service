package myaong.popolog.prejobsservice.feign.dto.request;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class PrejobsRequest {

	Long memberPrejobId;
	Long jobId;
	String jobName;
}

