package bot.managers;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class RoleIdManager {
    private String
            NormalID,
            LolID,
            TftID,
            ValID,
            McID,
            ChessID,
            PubgID,
            StdID,
            OwID,
            EtcID,
            MaleID,
            FemaleID,
            ID90,
            ID91,
            ID92,
            ID93,
            ID94,
            ID95,
            ID96,
            ID97,
            ID98,
            ID99,
            ID00,
            ID01,
            ID02,
            ID03,
            ID04,
            ID05,
            ID06,
            ID07,
            ID08,
            ID09,
            ID10,
            ID11,
            IDL11;


    public RoleIdManager() {
        settingID();
    }

    // RoleId.properties is hidden in gitHub repository
    private void settingID() {
        try (InputStream input = getClass().getClassLoader().getResourceAsStream("RoleID.properties")) {
            if (input == null) {
                throw new IOException ("❌ RoleID.properties 파일을 찾을 수 없습니다.");
            }

            Properties properties = new Properties();
            properties.load(input);

            NormalID = properties.getProperty("NormalID");
            LolID = properties.getProperty("LolID");
            TftID = properties.getProperty("TftID");
            ValID = properties.getProperty("ValID");
            McID = properties.getProperty("McID");
            ChessID = properties.getProperty("ChessID");
            PubgID = properties.getProperty("PubgID");
            StdID = properties.getProperty("StdID");
            OwID = properties.getProperty("OwID");
            EtcID = properties.getProperty("EtcID");
            MaleID = properties.getProperty("MaleID");
            FemaleID = properties.getProperty("FemaleID");
            ID90 = properties.getProperty("ID90");
            ID91 = properties.getProperty("ID91");
            ID92 = properties.getProperty("ID92");
            ID93 = properties.getProperty("ID93");
            ID94 = properties.getProperty("ID94");
            ID95 = properties.getProperty("ID95");
            ID96 = properties.getProperty("ID96");
            ID97 = properties.getProperty("ID97");
            ID98 = properties.getProperty("ID98");
            ID99 = properties.getProperty("ID99");
            ID00 = properties.getProperty("ID00");
            ID01 = properties.getProperty("ID01");
            ID02 = properties.getProperty("ID02");
            ID03 = properties.getProperty("ID03");
            ID04 = properties.getProperty("ID04");
            ID05 = properties.getProperty("ID05");
            ID06 = properties.getProperty("ID06");
            ID07 = properties.getProperty("ID07");
            ID08 = properties.getProperty("ID08");
            ID09 = properties.getProperty("ID09");
            ID10 = properties.getProperty("ID10");
            ID11 = properties.getProperty("ID11");
            IDL11 = properties.getProperty("IDL11");

        } catch (IOException e) {
            throw new RuntimeException("RoleID 설정 중 오류 발생", e);
        }
    }

    public String getNormalID() {
        return NormalID;
    }

    public String getLolID() {
        return LolID;
    }

    public String getTftID() {
        return TftID;
    }

    public String getValID() {
        return ValID;
    }

    public String getMcID() {
        return McID;
    }

    public String getChessID() {
        return ChessID;
    }

    public String getPubgID() {
        return PubgID;
    }

    public String getStdID() {
        return StdID;
    }

    public String getOwID() {
        return OwID;
    }

    public String getEtcID() {
        return EtcID;
    }

    public String getMaleID() {
        return MaleID;
    }

    public String getFemaleID() {
        return FemaleID;
    }

    public String getID90() {
        return ID90;
    }

    public String getID91() {
        return ID91;
    }

    public String getID92() {
        return ID92;
    }

    public String getID93() {
        return ID93;
    }

    public String getID94() {
        return ID94;
    }

    public String getID95() {
        return ID95;
    }

    public String getID96() {
        return ID96;
    }

    public String getID97() {
        return ID97;
    }

    public String getID98() {
        return ID98;
    }

    public String getID99() {
        return ID99;
    }

    public String getID00() {
        return ID00;
    }

    public String getID01() {
        return ID01;
    }

    public String getID02() {
        return ID02;
    }

    public String getID03() {
        return ID03;
    }

    public String getID04() {
        return ID04;
    }

    public String getID05() {
        return ID05;
    }

    public String getID06() {
        return ID06;
    }

    public String getID07() {
        return ID07;
    }

    public String getID08() {
        return ID08;
    }

    public String getID09() {
        return ID09;
    }

    public String getID10() {
        return ID10;
    }

    public String getID11() {
        return ID11;
    }

    public String getIDL11() {
        return IDL11;
    }
}

