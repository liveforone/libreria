package libreria.libreria.user.repository;

import libreria.libreria.user.model.Role;
import libreria.libreria.user.model.Users;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface UserRepository extends JpaRepository<Users, Long> {

    Users findByEmail(String email);

    //== 권한 업데이트 ==//
    @Modifying
    @Query("update Users u set u.auth = :auth where u.email = :email")
    void updateAuth(@Param("auth") Role auth, @Param("email") String email);

    //== 주소 업데이트 ==//
    @Modifying
    @Query("update Users u set u.address = :address where u.email = :email")
    void updateAddress(@Param("address") String address , @Param("email") String email);

    //== 주문횟수 업데이트 - 주문 완료 ==//
    @Modifying
    @Query("update Users u set u.count = u.count + 1 where u.email = :email")
    void plusCount(@Param("email") String email);

    //== 주문횟수 업데이트 - 주문 취소 ==//
    @Modifying
    @Query("update Users u set u.count = u.count - 1 where u.email = :email")
    void minusCount(@Param("email") String email);

    @Modifying
    @Query("update Users u set u.email = :newEmail where u.email = :oldEmail")
    void updateEmail(@Param("oldEmail") String oldEmail, @Param("newEmail") String newEmail);

    @Modifying
    @Query("update Users u set u.password = :password where u.id = :id")
    void updatePassword(@Param("id") Long id, @Param("password") String password);
}
