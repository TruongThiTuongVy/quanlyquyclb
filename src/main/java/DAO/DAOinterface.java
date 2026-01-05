package DAO;
import java.util.ArrayList;

public interface DAOinterface<T> {

    public int insert(T t);

    public int update(T t);

    public boolean delete(T t);

    public ArrayList<T> selectAll();

    public T selectById(T t);




}
