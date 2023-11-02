package org.example;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;

public class InterfaceListaTarefas {
    private static ArrayList<String> tarefas = new ArrayList<String>();
    private static DefaultListModel<String> model = new DefaultListModel<>();

    public static void main(String[] args) throws Exception {

        Connection connection = DriverManager.getConnection(
                "jdbc:postgresql://localhost:5432/listaTarefas",
                "seuUser",
                "suaSenha");

        JFrame frame = new JFrame("Aplicativo de Lista de Tarefas");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(400, 400);


        JList<String> list = new JList<>(model);
        JScrollPane scrollPane = new JScrollPane(list);

        JTextField textField = new JTextField();
        JButton addButton = new JButton("+");
        addButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try{
                    adicionarTarefa(connection, textField.getText());
                    textField.setText("");
                }catch (Exception ex){
                    ex.printStackTrace();
                }
            }
        });

        JButton removeButton = new JButton("Remover tarefa");
        removeButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    removerTarefa(connection, list.getSelectedValue());
                }catch (Exception ex){
                    ex.printStackTrace();
                }
            }
        });

        JPanel panel = new JPanel(new BorderLayout());
        panel.add(textField, BorderLayout.CENTER);
        panel.add(addButton, BorderLayout.EAST);

        frame.getContentPane().add(panel, BorderLayout.NORTH);
        frame.getContentPane().add(scrollPane, BorderLayout.CENTER);
        frame.getContentPane().add(removeButton, BorderLayout.SOUTH);
        frame.setLocationRelativeTo(null);

        carregarTarefas(connection);
        frame.setVisible(true);
    }

    private static void adicionarTarefa(Connection connection, String tarefa) throws Exception {
        PreparedStatement statement = connection.prepareStatement("INSERT INTO tarefas (descricao) VALUES (?)");
        statement.setString(1, tarefa);
        statement.executeUpdate();

        model.addElement(tarefa);
    }

    private static void removerTarefa(Connection connection, String tarefa) throws Exception {
        PreparedStatement statement = connection.prepareStatement("DELETE FROM tarefas WHERE descricao = ?");
        statement.setString(1, tarefa);
        statement.executeUpdate();


        model.removeElement(tarefa);
    }

    private static void carregarTarefas(Connection connection) throws Exception {
        PreparedStatement statement = connection.prepareStatement("SELECT descricao FROM tarefas");
        ResultSet resultSet = statement.executeQuery();

        while (((ResultSet) resultSet).next()) {
            model.addElement(resultSet.getString("descricao"));
        }

    }
}