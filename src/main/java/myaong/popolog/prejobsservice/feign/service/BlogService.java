package myaong.popolog.prejobsservice.feign.service;

import lombok.RequiredArgsConstructor;
import myaong.popolog.prejobsservice.entity.PreferredJob;
import myaong.popolog.prejobsservice.feign.client.BlogServiceClient;
import myaong.popolog.prejobsservice.feign.dto.request.PrejobsRequest;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class BlogService {

	private final BlogServiceClient blogServiceClient;

	public void syncPrejobs(Long memberId, List<PreferredJob> prejobs) {

		List<PrejobsRequest> requests = new ArrayList<>();
		for (PreferredJob prejob : prejobs) {
			PrejobsRequest request = PrejobsRequest.builder()
					.memberPrejobId(prejob.getId())
					.jobId(prejob.getJob().getId())
					.jobName(prejob.getJob().getName())
					.build();
			requests.add(request);
		}

		blogServiceClient.createPrejobs(memberId, requests);
	}
}
