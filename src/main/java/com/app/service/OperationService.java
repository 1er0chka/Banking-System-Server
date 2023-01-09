package com.app.service;

import com.app.DAO.OperationDAO;
import com.app.model.Operation;

import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;

public class OperationService implements Service<Operation> {
    @Override
    public Operation get(int id) {
        for (Operation tempOperation : getAll()) {
            if (tempOperation.getId() == id) {
                return tempOperation;
            }
        }
        return null;
    }

    @Override
    public ArrayList<Operation> getAll() {
        OperationDAO operationDAO = new OperationDAO();
        return operationDAO.readAll();
    }

    @Override
    public String add(Operation operation) throws NoSuchAlgorithmException {
        OperationDAO operationDAO = new OperationDAO();
        operationDAO.create(operation);
        return null;
    }

    @Override
    public String update(Operation operation) {
        OperationDAO operationDAO = new OperationDAO();
        operationDAO.update(operation);
        return null;
    }

    @Override
    public void delete(Operation operation) {
        OperationDAO operationDAO = new OperationDAO();
        operationDAO.delete(operation);
    }
}
