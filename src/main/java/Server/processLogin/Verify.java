package Server.processLogin;

import Server.ChatServer;
import config.DbUtil;
import message.FindMessage;
import message.RequestMessage;
import message.StringMessage;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class Verify {
    public static boolean verifyPassword(FindMessage msg) throws SQLException {//查询密码的对错
        DbUtil db = DbUtil.getDb();
        Connection conn = db.getConn();
        conn.prepareStatement("use members").execute();
        PreparedStatement ps = conn.prepareStatement("select password from members.user where uid = ? limit 1;");
        ps.setObject(1,msg.getUid());
        ResultSet rs = ps.executeQuery();
        rs.next();
        return rs.getString("password").equals(msg.getPassword());
    }

    public static boolean verifyIsFriend(RequestMessage msg) throws SQLException {//查询是否已经是好友
        DbUtil db = DbUtil.getDb();
        Connection conn = db.getConn();
        conn.prepareStatement("use members").execute();

        PreparedStatement ps = conn.prepareStatement("select second_uid from members.friends where first_uid = ? and second_uid = ?");
        ps.setObject(1,msg.getRecipientPerson().getUid());
        ps.setObject(2,msg.getRequestPerson().getUid());
        ResultSet rs = ps.executeQuery();
        if(rs.next()){
            String uid = rs.getString("second_uid");
            if(!uid.equals(msg.getRequestPerson().getUid())){
                throw new SQLException("friends表数据异常:"+msg.getRecipientPerson().getUid()+","+msg.getRequestPerson().getUid());
            }
        }else{
            return false;
        }

        ps = conn.prepareStatement("select first_uid from members.friends where first_uid = ? and second_uid = ?");
        ps.setObject(1,msg.getRecipientPerson().getUid());
        ps.setObject(2,msg.getRequestPerson().getUid());
        rs = ps.executeQuery();
        if(rs.next()){
            String uid = rs.getString("first_uid");
            if(!uid.equals(msg.getRecipientPerson().getUid())){
                throw new SQLException("friends表数据异常:"+msg.getRequestPerson().getUid()+","+msg.getRecipientPerson().getUid());
            }
        }else{
            return false;
        }

        return true;
    }

    public static boolean verifyBlack(StringMessage sm) throws SQLException {//查找接收方
        Connection conn = DbUtil.getDb().getConn();
        PreparedStatement ps;

        ps = conn.prepareStatement("select black from members.friends where first_uid = ? and second_uid = ?");
        ps.setObject(1,sm.getFriend().getUid());
        ps.setObject(2,sm.getMe().getUid());
        ResultSet rs = ps.executeQuery();
        if(rs.next() ){
            boolean black = rs.getBoolean("black");
            if(black){
                ChatServer.addBlack(sm.getFriend().getUid(),sm.getMe().getUid());
                return true;
            }
        }

        return false;
    }

}
