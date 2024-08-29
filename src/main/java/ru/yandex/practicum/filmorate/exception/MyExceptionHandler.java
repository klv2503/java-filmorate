package ru.yandex.practicum.filmorate.exception;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class MyExceptionHandler {

    /*
    Логгирование я решил пока вести через консоль. Но в консоли было много шума из-за полных стектрейсов
    генерируемых контроллерами "своих" исключений, хотя здесь вполне достаточно вывести класс-метод-линию. После
    написания обработчика программа не проходила несколько тестов, предложенных в ТЗ, т.к. вначале я не знал,
    как вывести @ResponseBody. В итоге я нашел такое решение: прицепить нужные данные к генерируемому исключению
     */
    private static final Logger log = LoggerFactory.getLogger(MyExceptionHandler.class);

    @ExceptionHandler({ValidationException.class, DuplicateData.class, NotFoundException.class})
    public ResponseEntity<Object> handler(ValidationException e) {
        StackTraceElement sElem = e.getStackTrace()[0];
        String className = sElem.getClassName();
        String str = className.substring(className.lastIndexOf(".") + 1);
        log.info("Class: {}; Method: {}; Line: {}; Message: {}",
                str, sElem.getMethodName(), sElem.getLineNumber(), e.getMessage());
        return new ResponseEntity<>(e.getObjForBody(),
                e.getClass().equals(NotFoundException.class) ?
                        HttpStatus.INTERNAL_SERVER_ERROR : HttpStatus.BAD_REQUEST);
    }
}
