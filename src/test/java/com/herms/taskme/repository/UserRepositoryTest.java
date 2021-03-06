package com.herms.taskme.repository;


import javax.validation.ConstraintViolationException;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import com.herms.taskme.model.User;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertThrows;

@ExtendWith(SpringExtension.class)
@DataJpaTest
public class UserRepositoryTest {

	@Autowired
	private UserRepository userRepository;
	
	@Test
	public void whenCreateShouldPersistData() {
		User user = new User(null, "Emerson", "Ribeiro Junior", "994457676", "Rua Joana da Silva, 139, Uberlândia", "herms", "123456");
		this.userRepository.save(user);
		assertThat(user.getId()).isNotNull();
		assertThat(user.getGivenName()).isEqualTo("Emerson");
		assertThat(user.getFamilyName()).isEqualTo("Ribeiro Junior");
		assertThat(user.getContact()).isEqualTo("994457676");
		assertThat(user.getAddress()).isEqualTo("Rua Joana da Silva, 139, Uberlândia");
		assertThat(user.getUsername()).isEqualTo("herms");
		assertThat(user.getPassword()).isEqualTo("123456");
	}

	@Test
	public void whenDeleteShouldRemoveData() {
		User user = new User(null, "Emerson", "Ribeiro Junior", "994457676", "Rua Joana da Silva, 139, Uberlândia", "herms", "123456");
		this.userRepository.save(user);
		this.userRepository.delete(user);
		assertThat(userRepository.findById(user.getId())).isEmpty();
	}

	@Test
	public void whenUpdateShouldChangeAndPersistData() {
		User user = new User(null, "emerson", "Ribeiro Junior", "994457676", "Rua Joana da Silva, 139, Uberlândia", "herms", "123456");
		this.userRepository.save(user);
		user.setContact("998877873");
		user.setGivenName("Emerson");
		this.userRepository.save(user);

		user = this.userRepository.findById(user.getId()).orElse(null);
		assertThat(user.getContact()).isEqualTo("998877873");
		assertThat(user.getGivenName()).isEqualTo("Emerson");
	}
	
	@Test
	public void whenfindByUsernameShouldIgnoreCase() {
		User user = new User(null, "Emerson", "Ribeiro Junior", "994457676", "Rua Joana da Silva, 139, Uberlândia", "herms", "123456");
		this.userRepository.save(user);
		User userFromLowerCase = this.userRepository.findByUsername("herms");
		User userFromUpperCase = this.userRepository.findByUsername("Herms");

		assertThat(userFromLowerCase).isEqualTo(userFromUpperCase);
	}
	
	@Test
	public void whenCreateWhenUsernameIsNullShouldThrownConstraintViolationException() {
		User user = new User(null, null, "Ribeiro Junior", "994457676", "Rua Joana da Silva, 139, Uberlândia", "teste", "123456");
		Exception exception = assertThrows(
							ConstraintViolationException.class,
							() -> this.userRepository.saveAndFlush(user));
		assertTrue(exception.getMessage().contains("Given name is a mandatory field, please fill it"));
	}

}
