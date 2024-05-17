package service;

import entities.CodePromo;

import java.sql.SQLException;
import java.util.List;

public interface IServiceCodePromo <C>{
    void addCodePromo(CodePromo c);

    List<CodePromo> readCodePromo() throws SQLException;

    void deleteCodePromo(int id);


    void modifyCodePromo(CodePromo c);
}
