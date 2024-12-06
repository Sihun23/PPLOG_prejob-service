package myaong.popolog.prejobsservice.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "`preferred_job`",
		uniqueConstraints = {@UniqueConstraint(columnNames = {"member_id", "job_id"})})
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class PreferredJob extends BaseEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "preferred_job_id")
	private Long id;

	@Column(name = "member_id", nullable = false, updatable = false)
	private Long memberId;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "job_id", nullable = false, updatable = false)
	private Job job;

	@Builder
	public PreferredJob(Long memberId, Job job) {
		this.memberId = memberId;
		this.job = job;
	}
}
