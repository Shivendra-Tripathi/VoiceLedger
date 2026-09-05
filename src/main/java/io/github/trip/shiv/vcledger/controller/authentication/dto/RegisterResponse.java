package io.github.trip.shiv.vcledger.controller.authentication.dto;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class RegisterResponse {
	
	private Long id;
	private String name ;
	private String email;
	private LocalDateTime createdAt;
}
