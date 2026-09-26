package io.github.trip.shiv.vcledger.core.dtos.projections;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class CustomerNameAndIdProjection {
	
	private final Long id;
	private final String name;
}
