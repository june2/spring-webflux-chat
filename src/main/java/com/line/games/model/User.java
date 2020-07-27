package com.line.games.model;

import lombok.*;


@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class User {
	private String email;
	private String name;
	private String password;
}
