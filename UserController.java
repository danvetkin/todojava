package ToDoList.Presentation.Controllers;

import ToDoList.Application.Exceptions.CustomExceptions.KeyNotFoundException;
import ToDoList.Application.Repositories.ModelsDTO.Token.TokenResponseModel;
import ToDoList.Application.Repositories.ModelsDTO.User.UserCreateModel;
import ToDoList.Application.Repositories.ModelsDTO.User.UserLoginDataModel;
import ToDoList.Domain.Services.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController // Аннотация Spring, указывающая, что этот класс является REST-контроллером
@RequestMapping("/api/v1/users") // Устанавливает базовый маршрут для всех методов контроллера
public class UserController {

    private UserService _userService; // Сервис, отвечающий за бизнес-логику, связанную с пользователями

    // Внедрение зависимости через конструктор
    public UserController(UserService userService) {
        _userService = userService;
    }

    /**
     * Аутентификация пользователя (вход в систему)
     * param userLoginDataModel объект с логином и паролем
     * return токен в ответе (если логин/пароль корректны)
     * throws KeyNotFoundException если пользователь не найден или данные некорректны
     */
    @PostMapping("auth")
    public ResponseEntity<TokenResponseModel> AuthorizeUser(@RequestBody UserLoginDataModel userLoginDataModel)
            throws KeyNotFoundException {

        // Вызывает сервис авторизации и возвращает токен
        return ResponseEntity.ok(_userService.authorizeUser(userLoginDataModel));
    }

    /**
     * Регистрация нового пользователя
     * param userCreateModel объект с данными нового пользователя
     * return токен в ответе (пользователь сразу авторизуется)
     */
    @PostMapping
    public ResponseEntity<TokenResponseModel> CreateUser(@RequestBody UserCreateModel userCreateModel) {

        // Создаёт пользователя и возвращает токен авторизации
        return ResponseEntity.ok(_userService.createUser(userCreateModel));
    }
}
