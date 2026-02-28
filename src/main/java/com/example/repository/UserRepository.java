package com.example.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.JpaSort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.QueryRewriter;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Repository;
import com.example.entity.User;

import java.util.List;
import java.util.stream.Stream;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

	/**
	 * <li> В качестве projection лучше использовать интерфейсы, т.к в этом случае будут создаваться {@code JDK Proxy}
	 * над структурой результата выборки, что является более оптимальным по сравнению с использованием projection-объекта
	 * ввиду отсутствия необходимости дополнительно создавать новый экземпляр такого объекта для каждой записи.
	 * <li> При явном перечислении полей в выборке — будет строиться proxy над {@link jakarta.persistence.Tuple}.
	 * При использовании projection-интерфейса нужно также явно указывать {@code alias} для полей,
	 * т.к. в реализации Tuple Hibernate почему-то не распознает имена полей сущности для {@code alias},
	 * в результате будут возвращаться @{code null} при обращении к projection-полям.
	 * <li> Без перечисления полей — будет создаваться полноценный экземпляр сущности и над ним будет строиться proxy.
	 * <li> DTO-проекции используют позиционный маппинг через конструктор.
	 */
	@Query(value = "SELECT u.name name, u.username username FROM User u")
	<T> List<T> findProjection(Class<T> projectionType);


	@Query(value = "SELECT u FROM User u WHERE u.id % 2 <> 0 ORDER BY u.id")
	<T> Stream<T> findProjection();


	@Query(value = "SELECT u FROM #{#entityName} u" , queryRewriter = Rewriter.class)
	Page<User> findAll(Pageable pageable);


	@Query(value = "SELECT u FROM #{#entityName} u")
	List<User> findAll(JpaSort sort);


	@Query(value = "SELECT u.name as name, u.email as email FROM #{#entityName} u WHERE u.email = :email")
	<T> T findByEmail(@Param("email") String email, Class<T> type);


	@Component
	class Rewriter implements QueryRewriter {

		@Override
		public String rewrite(String query, Sort sort) {
			return query;
		}

		@Override
		public String rewrite(String query, Pageable pageRequest) {
			return QueryRewriter.super.rewrite(query, pageRequest);
		}
	}

}
