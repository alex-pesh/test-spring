package com.example.test.unit;

import com.example.entity.User;
import com.example.repository.UserRepository;
import com.example.service.ExampleServiceImpl;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import com.example.service.AuthService;
import com.example.service.ExampleService;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

import java.util.List;


/**
 * Тестирование сервиса с использованием Mockito
 * <p/>
 * Чтобы Mockito сам создал необходимые mock-объекты, необходимо добавить расширение Mockito к классу модульных тестов
 * при помощи аннотации <code>@ExtendsWith(MockitoExtension.class)</code>, а также отметить аннотацией
 * <code>@Mock</code> члены тестового класса, для которых требуется создать mock-объекты. Чтобы Mockito создал
 * экземпляр тестируемого класса и передал mock-объекты в качестве аргументов его конструктора, соответствующий член
 * тестового класса должен быть отмечен аннотацией <code>@InjectMocks</code>.
 * <p>
 * https://alexkosarev.name/2022/12/29/spring-in-a-nutshell-testing-rest-services/
 */

@ExtendWith(MockitoExtension.class)
public class AuthServiceTest {

	@Mock
	UserRepository userRepository;

	@InjectMocks
	ExampleServiceImpl exampleService;


	@Test
	@DisplayName("Тестирование...")
	public void exampleServiceTest() {

		assertNotNull(userRepository);

		List mockedList = mock(List.class);
		doReturn(mockedList).when(userRepository).findAll();

		exampleService.test();

	}

}
