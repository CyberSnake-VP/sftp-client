package org.sftpclient;

import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.SftpException;
import org.sftpclient.input.ConnectionInfoReader;
import org.sftpclient.model.ConnectionInfo;
import org.sftpclient.service.AddressService;
import org.sftpclient.sftp.SftpClient;
import org.sftpclient.ui.ConsoleMenu;
import org.sftpclient.validator.AddressValidator;

import java.io.IOException;
import java.util.Scanner;

/// Класс оркестратор, создает и управляет вызовами. Обработка ошибок.
public class SftpApp {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        // Читаем ввод пользователя, получаем объект для клиента
        ConnectionInfoReader infoReader = new ConnectionInfoReader(scanner);
        // Контейнер с данными для нашего клиента
        ConnectionInfo connectionInfo = infoReader.read();
        // Создаем SFTP клиента
        SftpClient client = new SftpClient(connectionInfo);
        // Валидатор адресов
        AddressValidator addressValidator = new AddressValidator();
        // Создаем сервис для работы с адресами
        AddressService addressService = new AddressService(client, addressValidator, "upload/addresses.json");
        // Создаем меню
        ConsoleMenu menu = new ConsoleMenu(scanner, addressService);

        try {
            client.connect();

            System.out.println("Connected successfully");

            menu.start();

        } catch (JSchException e) {
            System.out.println("Could not connect to SFTP server");
            System.out.println("Reason: " + e);
        } catch (IOException | SftpException e) {
            System.out.println("Operation failed");
            System.out.println("Reason: " + e);
        } finally {
            client.disconnect();
        }
    }

}
