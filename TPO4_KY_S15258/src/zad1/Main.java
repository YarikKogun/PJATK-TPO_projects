/**
 *
 *  @author Kohun Yaroslav S15258
 *
 */

package zad1;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;


@WebServlet("/Servlet")
public class Main extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private String url = "jdbc:derby://localhost/ksidb";
	private Connection connection;
	private PrintWriter printWriter;
	private boolean byBookName;

	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		try {
			
			String topHtml = "<title>Books</title> "
					+ "<body bgcolor=\"#FFFFFF\"> "
					+ "<h1>Books</h1> "
					+ "<form action = \"Servlet\">\r\n "
					+ "<select name = \"by\">\r\n"
					+ "<option>by book name</option>\r\n "
					+ "<option>by author</option>\r\n "
					+ "</select>\r\n "
					+ "<input type=\"text\" name =\"name\">\r\n "
					+ "<input type=\"submit\" value=\"find\">\r\n \r\n "
					+ "</form> "
					+ "</body> ";
					
			printWriter = response.getWriter();
			printWriter.println(topHtml);

			List<String> books;
			
			if (!request.getParameter("name").isEmpty()) {
				if (request.getParameter("by").equalsIgnoreCase("by author")) {
					byBookName = false;
				} else {
					byBookName = true;
				}
				printWriter = response.getWriter();
				books = getBooks(request.getParameter("name"));
				for(String item : books) {
					printWriter.append(item+"<br>"); 
				}		
				} else {
				printWriter = response.getWriter();
				books = getBooks("allBooks");
				for(String item : books) {
					printWriter.append(item+"<br>"); 
				}	
			}
			printWriter.close();
		} catch (Exception ex){}

	}

	private List<String> getBooks(String parameter) {
		List<String> books = new ArrayList<String>();
		if (parameter.equals("allBooks")) {
			try {
				Class.forName("org.apache.derby.jdbc.ClientDriver");
				connection = DriverManager.getConnection(url);
				connection.getMetaData();

				Statement statement = connection.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_UPDATABLE);
				ResultSet resultSet = statement.executeQuery(
						"SELECT AUTOR.NAME, POZYCJE.TYTUL, POZYCJE.ROK  "
						+ "FROM autor, pozycje "
						+ "WHERE POZYCJE.AUTID = AUTOR.AUTID");
				resultSet.afterLast();
				while (resultSet.previous()) {
					//String strBook = String.format("%-40s%-70s%-20s%n", "Author: "+ resultSet.getString(1),"Title: " +resultSet.getString(2),"Year: "+resultSet.getString(3));
					//System.out.println(strBook);
					//books.add(strBook);
					books.add(("Author: "+ resultSet.getString(1) + " ___ Title: " + resultSet.getString(2) + " ___ Year: " + resultSet.getString(3)));
				}
				Collections.sort(books);
				if (connection != null)
					connection.close();
			} catch (Exception ex) {
				ex.printStackTrace();
			}
		} else if(!byBookName && !parameter.equals("allBooks")) {
			try {
				Class.forName("org.apache.derby.jdbc.ClientDriver");
				connection = DriverManager.getConnection(url);
				connection.getMetaData();

				Statement statement = connection.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_UPDATABLE);
				ResultSet resultSet = statement.executeQuery(
						"SELECT AUTOR.NAME, POZYCJE.TYTUL, POZYCJE.ROK  "
						+ "FROM autor, pozycje "
						+ "WHERE POZYCJE.AUTID = AUTOR.AUTID "
						+ "AND AUTOR.NAME LIKE \'" + parameter + "%\'");
				resultSet.afterLast();
				while (resultSet.previous()) {
					//String strBook = String.format("%-40s%-70s%-20s%n", "Author: "+ resultSet.getString(1),"Title: " +resultSet.getString(2),"Year: "+resultSet.getString(3));
					//System.out.println(strBook);
					//books.add(strBook);
					books.add(("Author: "+ resultSet.getString(1) + " ___ Title: " + resultSet.getString(2) + " ___ Year: " + resultSet.getString(3)));
				}
				Collections.sort(books);
				if (connection != null)
					connection.close();
			} catch (Exception ex) {
				ex.printStackTrace();
			}
		} else if(byBookName && !parameter.equals("allBooks")) {
			try {
				Class.forName("org.apache.derby.jdbc.ClientDriver");
				connection = DriverManager.getConnection(url);
				connection.getMetaData();

				Statement statement = connection.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_UPDATABLE);
				ResultSet resultSet = statement.executeQuery(
						"SELECT AUTOR.NAME, POZYCJE.TYTUL, POZYCJE.ROK  "
						+ "FROM autor, pozycje "
						+ "WHERE POZYCJE.AUTID = AUTOR.AUTID "
						+ "AND POZYCJE.TYTUL LIKE \'" + parameter + "%\'");
				resultSet.afterLast();
				while (resultSet.previous()) {
					//String strBook = String.format("%-40s%-70s%-20s%n", "Author: "+ resultSet.getString(1),"Title: " +resultSet.getString(2),"Year: "+resultSet.getString(3));
					//System.out.println(strBook);
					//books.add(strBook);
					books.add(("Author: "+ resultSet.getString(1) + " ___ Title: " + resultSet.getString(2) + " ___ Year: " + resultSet.getString(3)));
				}
				Collections.sort(books);
				if (connection != null)
					connection.close();
			} catch (Exception ex) {
				ex.printStackTrace();
			}
		} 
		return books;
	}

}
