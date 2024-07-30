package com.tooltracker.backend.walltools.exceptions;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import com.tooltracker.backend.walltools.dtos.ErrorDTO;
import com.tooltracker.backend.walltools.dtos.ResponseShape;
import com.tooltracker.backend.walltools.enums.ResponseStatus;

@ControllerAdvice
public class WallToolsExceptionsHandler { // err 12

        private static final org.slf4j.Logger meme = org.slf4j.LoggerFactory
                        .getLogger(WallToolsExceptionsHandler.class);

        // #region General Exceptions

        @ExceptionHandler(MethodArgumentNotValidException.class)
        public ResponseEntity<ResponseShape> catchMethodArgumentNotValidException(MethodArgumentNotValidException e) {
                return new ResponseEntity<>(
                                new ResponseShape(ResponseStatus.FAILED, new ErrorDTO("#3", e.getMessage())),
                                HttpStatus.BAD_REQUEST);
        }

        @ExceptionHandler(IllegalArgumentException.class)
        public ResponseEntity<ResponseShape> catchIllegalArgumentException(IllegalArgumentException e) {
                return new ResponseEntity<>(
                                new ResponseShape(ResponseStatus.FAILED, new ErrorDTO("#4", e.getMessage())),
                                HttpStatus.BAD_REQUEST);
        }

        @ExceptionHandler(HttpMessageNotReadableException.class) // Invalid payload content
        public ResponseEntity<ResponseShape> catchHttpMessageNotReadableException(HttpMessageNotReadableException e) {
                return new ResponseEntity<>(
                                new ResponseShape(ResponseStatus.FAILED, new ErrorDTO("#5", e.getMessage())),
                                HttpStatus.BAD_REQUEST);
        }

        @ExceptionHandler(DataIntegrityViolationException.class) // when there is a is a violation of database integrity
                                                                 // constraints
        public ResponseEntity<ResponseShape> catchDataIntegrityViolationException(DataIntegrityViolationException e) {
                return new ResponseEntity<>(
                                new ResponseShape(ResponseStatus.FAILED, new ErrorDTO("#6", e.getMessage())),
                                HttpStatus.BAD_REQUEST);
        }

        /*
         * When using the scan to take some tools, the sensors will send a request with
         * a list of IDs. However, if one or more IDs do not already exist in the tools
         * table, we observe that the sensor may be sending incorrect data. In such
         * cases, the backend will throw an exception and prevent the operation.
         */

        @ExceptionHandler(SensorSendInvalidIDsException.class)
        public ResponseEntity<ResponseShape> catchSensorSendInvalidIDsException(SensorSendInvalidIDsException e) {
                return new ResponseEntity<>(
                                new ResponseShape(ResponseStatus.FAILED, new ErrorDTO("#7", e.getMessage())),
                                HttpStatus.BAD_REQUEST);

        }

        // #endregion

        // #region Associated with Tool Exceptions

        @ExceptionHandler(ToolNameIsExistedException.class)
        public ResponseEntity<ResponseShape> catchToolNameIsExistedException(ToolNameIsExistedException e) {
                meme.info("anas");
                return new ResponseEntity<>(
                                new ResponseShape(ResponseStatus.FAILED, new ErrorDTO("#1", e.getMessage())),
                                HttpStatus.BAD_REQUEST);
        }

        @ExceptionHandler(OngoingInProcessMovementException.class)
        public ResponseEntity<ResponseShape> catchThereAreInProcessingMovementException(
                        OngoingInProcessMovementException e) {
                return new ResponseEntity<>(
                                new ResponseShape(ResponseStatus.FAILED, new ErrorDTO("#10", e.getMessage())),
                                HttpStatus.BAD_REQUEST);
        }

        @ExceptionHandler(NotFoundToolIdException.class)
        public ResponseEntity<ResponseShape> catchNotFoundToolIdException(NotFoundToolIdException e) {
                return new ResponseEntity<>(
                                new ResponseShape(ResponseStatus.FAILED, new ErrorDTO("#2", e.getMessage())),
                                HttpStatus.BAD_REQUEST);
        }

        @ExceptionHandler(NotFoundMovementIdException.class)
        public ResponseEntity<ResponseShape> catchNotFoundMovementIdException(NotFoundMovementIdException e) {
                return new ResponseEntity<>(
                                new ResponseShape(ResponseStatus.FAILED, new ErrorDTO("#8", e.getMessage())),
                                HttpStatus.BAD_REQUEST);
        }

        @ExceptionHandler(ToolIsNotRecordedInMovementException.class)
        public ResponseEntity<ResponseShape> catchToolIsNotRecordedInMovementException(
                        ToolIsNotRecordedInMovementException e) {
                return new ResponseEntity<>(
                                new ResponseShape(ResponseStatus.FAILED, new ErrorDTO("#9", e.getMessage())),
                                HttpStatus.BAD_REQUEST);
        }

        @ExceptionHandler(CouldNotFoundInProcessingMovementException.class)
        public ResponseEntity<ResponseShape> catchCouldNotFoundInProcessingMovementException(
                        CouldNotFoundInProcessingMovementException e) {
                return new ResponseEntity<>(
                                new ResponseShape(ResponseStatus.FAILED, new ErrorDTO("#11", e.getMessage())),
                                HttpStatus.BAD_REQUEST);
        }

        @ExceptionHandler(ExceededInProcessingMovementLimitException.class)
        public ResponseEntity<ResponseShape> catchExceededInProcessingMovementLimitException(
                        ExceededInProcessingMovementLimitException e) {
                return new ResponseEntity<>(
                                new ResponseShape(ResponseStatus.FAILED, new ErrorDTO("#12", e.getMessage())),
                                HttpStatus.BAD_REQUEST);
        }

        // #endregion

}
