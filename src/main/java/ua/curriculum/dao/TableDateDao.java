package ua.curriculum.dao;

import ua.curriculum.fx.ComboBoxItem;

import java.sql.SQLException;
import java.util.List;

public interface TableDateDao<T> {
    T findById(Integer id) throws SQLException;

    boolean insert(T t) throws SQLException;

    boolean update(T t) throws SQLException;

    boolean deleteById(Integer id) throws SQLException;

    List<T> findAllData() throws SQLException;

    List<ComboBoxItem> findAllComboBoxData() throws SQLException;

    ComboBoxItem findComboBoxDataById(Integer id) throws SQLException;
}
