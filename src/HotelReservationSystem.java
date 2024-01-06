import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Connection;
import java.util.Scanner;
import java.sql.Statement;
import java.sql.ResultSet;

public class HotelReservationSystem {

        private static final String url="jdbc:mysql://localhost:3306/HotelReservation";
        private static final String username="root";
        private static final String password="12345";
        public static void main(String [] args) throws ClassNotFoundException ,SQLException
        {
            try
            {
                Class.forName("com.mysql.cj.jdbc.Driver");
            }
            catch (ClassNotFoundException e)
            {
                System.out.println(e.getMessage());
            }
            try(Connection connection=DriverManager.getConnection(url,username,password)){
                System.out.println("Connected Successfully");
                while(true)
                {
                    System.out.println();
                    System.out.println("HOTEL MANAGEMENT SYSTEM");
                    Scanner scanner=new Scanner(System.in);
                    System.out.println("Choose an Option:");
                    System.out.println("1.Reserve a Room:");
                    System.out.println("2.View Reservations");
                    System.out.println("3.Get Room Number");
                    System.out.println("4.Update Reservation");
                    System.out.println("5.Delete Reservation");
                    System.out.println("0.Exit");
                    int choice=scanner.nextInt();
                    switch (choice)
                    {
                        case 1:
                            ReserveRoom(connection,scanner);
                            break;
                        case 2:
                            ViewReservations(connection);
                            break;
                        case 3:
                            getRoomNumber(connection,scanner);
                            break;
                        case 4:
                            UpdateReservation(connection,scanner);
                            break;
                        case 5:
                            DeleteReservation(connection,scanner);
                            break;
                        case 0:
                            exit();
                            scanner.close();
                            return;
                        default:
                            System.out.println("Please Enter Valid Choice!!");
                    }
                }
            }
            catch (SQLException e){
                System.err.println("Connection Failed: "+e.getMessage());
            }
            catch (InterruptedException e)
            {
                throw new RuntimeException(e);//throw used when any particular statement is thrown as an exception
            }
        }
        private static void ReserveRoom(Connection connection,Scanner scanner)
        {
            try
            {
                System.out.println("Enter the Guest Name:");
                String name=scanner.next();
                System.out.println("Enter the Room Number:");
                int room=scanner.nextInt();
                System.out.println("Enter the Contact Number:");
                String contact=scanner.next();
                String sql="INSERT INTO resevations(guest_name,room_no,contact_no) "+
                        "VALUES ('" + name + "',"+ room +", '"+ contact + "')";


                try(Statement statement=connection.createStatement())
                {
                    int affectedRows=statement.executeUpdate(sql);
                    if(affectedRows>0)
                    {
                        System.out.println("Reservation Successful!!");
                    }
                    else
                    {
                        System.out.println("Reservation Failed");
                    }
                }
            }
            catch (SQLException e)
            {
                e.printStackTrace();
            }
        }
        private static void ViewReservations(Connection connection) throws SQLException
        {
            String sql ="SELECT *FROM resevations;";

            try(Statement statement=connection.createStatement();
            ResultSet resultSet=statement.executeQuery(sql))
            {
                System.out.println("Current Reservations:");
                System.out.println("+------------------+------------------------+--------------+----------------------+----------------------+");
                System.out.println("|   ReservationID  |        Guest Name      |  Room Number |    Contact Number    |   Reservation Date   |");
                System.out.println("+------------------+------------------------+--------------+----------------------+----------------------+");
                while (resultSet.next())
                {
                    int reservationId =resultSet.getInt("R_id");
                    String name =resultSet.getString("guest_name");
                    int room=resultSet.getInt("room_no");
                    String contact=resultSet.getString("contact_no");
                    String ReservationDate=resultSet.getTimestamp("reservation_date").toString();
                    System.out.printf("|%-17d | %-23s|%-13d | %-21s|%-21s |\n",reservationId,name,room,contact,ReservationDate);
                }
                System.out.println("+------------------+------------------------+------------+------------------------+----------------------+");
            }
        }
        private static void getRoomNumber(Connection connection,Scanner scanner)
        {
            try
            {
                System.out.println("Enter Reservation Id:");
                int rid=scanner.nextInt();
                System.out.println("Enter Guest Name:");
                String name=scanner.next();
                String sql = "SELECT room_no FROM resevations " +
                        "WHERE R_id = " + rid + " AND guest_name = '" + name + "'";
                try(Statement statement=connection.createStatement();
                ResultSet resultSet=statement.executeQuery(sql))
                {
                    if(resultSet.next())
                    {
                        int room=resultSet.getInt("room_no");
                        System.out.println("Room Number for Reservation Id "+rid+" and Guest "+name+" is: " +room);
                    }
                    else
                    {
                        System.out.println("Reservation not Found For Specified Name Or ID!!");
                    }
                }
            }
            catch (SQLException e)
            {
                e.printStackTrace();
            }
        }
        private static void UpdateReservation(Connection connection,Scanner scanner)
        {
            try
            {
                System.out.println("Enter Reservation ID to Update:");
                int rid=scanner.nextInt();
                scanner.nextLine();
                if(!reservationExists(connection,rid))
                {
                    System.out.println("Reservation Not Found for Specified Id!!");
                    return;
                }
                System.out.println("Enter New Guest Name:");
                String newname=scanner.nextLine();
                System.out.println("Enter New Room Number:");
                int newroom=scanner.nextInt();
                System.out.println("Enter Contact Number Of Guest:");
                String newContact=scanner.next() ;
                String sql = "UPDATE resevations SET " +
                        "guest_name = '" + newname + "', " +
                        "room_no = " + newroom + ", " +
                        "contact_no = '" + newContact + "' " +
                        "WHERE R_id = " + rid;

                try(Statement statement=connection.createStatement())
                {
                    int affectedRows=statement.executeUpdate(sql);
                    if(affectedRows>0)
                    {
                        System.out.println("Reservation Updated Successfully!!");
                    }
                    else
                    {
                        System.out.println("Oops Failed to Update Reservation!!");
                    }
                }
            }
            catch (SQLException e)
            {
                e.printStackTrace();
            }
        }
    private static void DeleteReservation(Connection connection,Scanner scanner)
    {
        try
        {
            System.out.println("Enter Reservation ID to Delete:");
            int rid=scanner.nextInt();
            scanner.nextLine();
            if(!reservationExists(connection,rid))
            {
                System.out.println("Reservation Not Found for Specified Id!!");
                return;
            }
            String sql="DELETE FROM resevations WHERE R_id= "+rid;
            try(Statement statement=connection.createStatement())
            {
                int affectedRows=statement.executeUpdate(sql);
                if(affectedRows>0)
                {
                    System.out.println("Reservation Deleted Successfully!!");
                }
                else
                {
                    System.out.println("Oops Failed to Delete Reservation!!");
                }
            }
        }
        catch (SQLException e)
        {
            e.printStackTrace();
        }
    }
    private  static boolean reservationExists(Connection connection,int rid)
    {
        try
        {
            String sql="SELECT R_id FROM resevations  WHERE R_id = "+rid;
            try(Statement statement=connection.createStatement();
            ResultSet resultSet=statement.executeQuery(sql))
            {
                return resultSet.next();
            }
        }
        catch (SQLException e)
        {
            e.printStackTrace();
            return false;
        }
    }
    public static void exit() throws  InterruptedException
    {
        System.out.println("Existing System\nPlease Wait");
        int i=5;
        while (i!=0)
        {
            System.out.println(".");
            Thread.sleep(500);
            i--;
        }
        System.out.println();
        System.out.println("THANK YOU FOR USING HOTEL RESERVATION SYSTEM!!");
    }
}
