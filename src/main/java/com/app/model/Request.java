package com.app.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class Request {
    private RequestType requestType;
    private String requestMessage;

    public enum RequestType {
        LogIn,
        LogOut,
        Registration,
        UpdateUser,
        GetAllUser,
        CreateAccount,
        UpdateAccount,
        DeleteAccount,
        CreateCard,
        DeleteCard,
        TransferCard,
        TransferAccount,
        Payment,
        Exit
    }
}
