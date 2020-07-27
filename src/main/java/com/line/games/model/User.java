package com.line.games.model;

import lombok.*;


@AllArgsConstructor
@NoArgsConstructor
@ToString
@Getter
@Setter
@Builder
public class User {
	private Long id;
	private String email;
	private String name;
	private String password;
}
