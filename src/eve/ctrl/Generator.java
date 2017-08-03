package eve.ctrl;


import eve.model.infra.Host;
import eve.model.infra.Network;
import eve.wuti.jpa.DAO;
import eve.wuti.jpa.Insert;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by jesus on 11/18/16.
 */
public class Generator {
    public static Map<Network, List<Host>> generateInfra() {
        Map<Network, List<Host>> result = new HashMap<>();

        // Red Team
        Network RT_SINET = new Network("RT_SINET", "10.0.247.0", "");
        List<Host> RT_SINETHosts = new ArrayList<>();
        for (int i = 10; i < 30; i++)
            RT_SINETHosts.add(new Host("kali" + i + ".rrt.ex", Host.Type.redTeamLinux, "10.0.247." + i, "", RT_SINET, 1007, 61));
        for (int i = 30; i < 41; i++)
            RT_SINETHosts.add(new Host("winhack" + i + ".rrt.ex", Host.Type.redTeamWindows, "10.0.247." + i, "", RT_SINET, 1111, 60));
        RT_SINETHosts.add(new Host("bmds.af-berilya.ex", Host.Type.redTeamWindows, "10.0.247.41", "", RT_SINET, 914, 115));
        result.put(RT_SINET, RT_SINETHosts);


        // GT_SINET
        Network GT_SINET = new Network("GT_SINET", "100.64.8.0", "2a07:1181:150::");
        List<Host> GT_SINETHosts = new ArrayList<>();
        GT_SINETHosts.add(new Host("www.rd-berilya", "www.rd-berilya.ex", Host.Type.serverLinux, "100.64.8.181", "2a07:1181:150:::181", GT_SINET, 213, 45));
        GT_SINETHosts.add(new Host("srv.rd-berilya", "srv.rd-berilya.ex", Host.Type.serverLinux, "100.64.8.182", "2a07:1181:150:::182", GT_SINET, 347, 45));
        GT_SINETHosts.add(new Host("shop", "shop.ex", Host.Type.serverLinux, "100.64.8.17", "2a07:1181:150:::17", GT_SINET, 465, 47));
        GT_SINETHosts.add(new Host("dashbest", "dashbest.ex", Host.Type.honeyPot, "100.64.8.171", "2a07:1181:150:::171", GT_SINET, 586, 46));
        GT_SINETHosts.add(new Host("dns", "dns1.ex", Host.Type.serverLinux, "100.64.8.2", "2a07:1181:150:::2", GT_SINET, 51, 165));
        GT_SINETHosts.add(new Host("dns2", "dns2.ex", Host.Type.serverLinux, "100.64.8.3", "2a07:1181:150:::3", GT_SINET, 156, 163));
        GT_SINETHosts.add(new Host("news", "news.ex", Host.Type.serverLinux, "100.64.8.33", "2a07:1181:150:::33", GT_SINET, 263, 163));
        GT_SINETHosts.add(new Host("vps", "vps.ex", Host.Type.serverLinux, "100.64.8.4", "2a07:1181:150:::4", GT_SINET, 373, 164));
        // xs-xtraffic.ex 477,167
        // trafgen-tgt1.ex 590,153
        result.put(GT_SINET, GT_SINETHosts);

        // DMZ
        Network DMZ = new Network("DMZ", "198.18.2.0", "2a07:1181:130:3602::");
        List<Host> dmzHosts = new ArrayList<>();
        dmzHosts.add(new Host("dns", Host.Type.serverLinux, "198.18.2.2", "2a07:1181:130:3602::2", DMZ, 248, 302));
        dmzHosts.add(new Host("www", Host.Type.serverLinux, "198.18.2.3", "2a07:1181:130:3602::3", DMZ, 387, 301));

        dmzHosts.add(new Host("mail", Host.Type.serverLinux, "198.18.2.4", "2a07:1181:130:3602::4", DMZ, 63, 442));
        dmzHosts.add(new Host("proxy", Host.Type.serverLinux, "198.18.2.5", "2a07:1181:130:3602::5", DMZ, 199, 442));
        dmzHosts.add(new Host("snickers", Host.Type.honeyPot, "198.18.2.36", "2a07:1181:130:3602::36", DMZ, 340, 440));
        result.put(DMZ, dmzHosts);

        // INT
        Network INT = new Network("INT", "10.242.4.0", "2a07:1181:130:3604::");
        List<Host> INTHosts = new ArrayList<>();
        INTHosts.add(new Host("dc", Host.Type.serverWindows, "10.242.4.2", "2a07:1181:130:3604::2", INT, 225, 574));
        INTHosts.add(new Host("files", Host.Type.serverWindows, "10.242.4.3", "2a07:1181:130:3604::3", INT, 63, 685));
        INTHosts.add(new Host("sql", Host.Type.serverLinux, "10.242.4.4", "2a07:1181:130:3604::4", INT, 205, 685));
        INTHosts.add(new Host("sharepoint", Host.Type.serverWindows, "10.242.4.6", "2a07:1181:130:3604::6", INT, 342, 684));
        INTHosts.add(new Host("hr", Host.Type.serverLinux, "10.242.4.7", "2a07:1181:130:3604::7", INT, 64, 801));
        INTHosts.add(new Host("w1", Host.Type.workStationWindows, "10.242.4.11", "2a07:1181:130:3604::11", INT, 197, 801));
        INTHosts.add(new Host("w2", Host.Type.workStationWindows, "10.242.4.12", "2a07:1181:130:3604::12", INT, 333, 801));
        INTHosts.add(new Host("w3", Host.Type.workStationWindows, "10.242.4.13", "2a07:1181:130:3604::13", INT, 69, 992));
        INTHosts.add(new Host("w4", Host.Type.workStationWindows, "10.242.4.14", "2a07:1181:130:3604::14", INT, 209, 992));
        INTHosts.add(new Host("w5", Host.Type.workStationWindows, "10.242.4.15", "2a07:1181:130:3604::15", INT, 345, 992));
        INTHosts.add(new Host("oxygen", Host.Type.workStationWindows, "10.242.4.30", "2a07:1181:130:3604::30", INT, 213, 1041));
        result.put(INT, INTHosts);

        // BAK
        Network BAK = new Network("BAK", "10.242.5.0", "2a07:1181:130:3605::");
        List<Host> BAKHosts = new ArrayList<>();
        BAKHosts.add(new Host("srv", Host.Type.serverWindows, "10.242.5.2", "2a07:1181:130:3605::2", BAK, 483, 957));
        BAKHosts.add(new Host("ops", Host.Type.serverLinux, "10.242.5.3", "2a07:1181:130:3605::3", BAK, 595, 957));
        result.put(BAK, BAKHosts);

        // DEV
        Network DEV = new Network("DEV", "10.242.6.0", "2a07:1181:130:3606::");
        List<Host> DEVHosts = new ArrayList<>();

        DEVHosts.add(new Host("srv1", Host.Type.serverLinux, "10.242.6.3", "2a07:1181:130:3606::3", DEV, 890, 891));
        DEVHosts.add(new Host("srv2", Host.Type.serverWindows, "10.242.6.4", "2a07:1181:130:3606::4", DEV, 1022, 891));
        DEVHosts.add(new Host("ops", Host.Type.serverLinux, "10.242.6.5", "2a07:1181:130:3606::5", DEV, 213, 46));
        DEVHosts.add(new Host("git", Host.Type.serverLinux, "10.242.6.2", "2a07:1181:130:3606::2", DEV, 745, 1004));
        DEVHosts.add(new Host("collab", Host.Type.serverWindows, "10.242.6.6", "2a07:1181:130:3606::6", DEV, 889, 1003));
        DEVHosts.add(new Host("exploits", Host.Type.serverWindows, "10.242.6.7", "2a07:1181:130:3606::7", DEV, 1021, 1004));
        result.put(DEV, DEVHosts);


        // LAB
        Network LAB = new Network("LAB", "10.242.7.0", "2a07:1181:130:3607::");
        List<Host> LABHosts = new ArrayList<>();
        LABHosts.add(new Host("test1", Host.Type.serverLinux, "10.242.7.4", "2a07:1181:130:3607::4", LAB, 1312, 893));
        LABHosts.add(new Host("test2", Host.Type.serverLinux, "10.242.7.5", "2a07:1181:130:3607::5", LAB, 1448, 893));
        LABHosts.add(new Host("srv-berilya", "srv-berilya.clf.ex", Host.Type.serverLinux, "10.242.7.2", "2a07:1181:130:3607::2", LAB, 1193, 994));
        LABHosts.add(new Host("www-berilya", "www-berilya.clf.ex", Host.Type.serverLinux, "10.242.7.3", "2a07:1181:130:3607::3", LAB, 1330, 994));
        LABHosts.add(new Host("chocolate", "chocolate.clf.ex", Host.Type.honeyPot, "10.242.7.77", "2a07:1181:130:3607::77", LAB, 1465, 994));
        result.put(LAB, LABHosts);


        // OPS
        Network OPS = new Network("OPS", "198.18.3.0", "2a07:1181:130:3603::");
        List<Host> OPSHosts = new ArrayList<>();
        OPSHosts.add(new Host("cnc", Host.Type.serverLinux, "198.18.3.2", "2a07:1181:130:3603::2", OPS, 934, 362));
        OPSHosts.add(new Host("ftp", Host.Type.serverLinux, "198.18.3.3", "2a07:1181:130:3603::3", OPS, 1068, 361));
        OPSHosts.add(new Host("nagios", Host.Type.serverLinux, "198.18.3.4", "2a07:1181:130:3603::4", OPS, 1202, 360));
        OPSHosts.add(new Host("pxe", Host.Type.serverLinux, "198.18.3.5", "2a07:1181:130:3603::5", OPS, 1335, 360));
        OPSHosts.add(new Host("log", Host.Type.serverLinux, "198.18.3.6", "2a07:1181:130:3603::6", OPS, 1467, 361));
        OPSHosts.add(new Host("dms", Host.Type.serverLinux, "198.18.3.7", "2a07:1181:130:3603::7", OPS, 213, 46));
        OPSHosts.add(new Host("drone", Host.Type.drone, "198.18.3.10", "2a07:1181:130:3603::10", OPS, 213, 46));
        // dcs.ops.clf.ex 763,481
        OPSHosts.add(new Host("beef", Host.Type.serverLinux, "198.18.3.8", "2a07:1181:130:3603::8", OPS, 900, 481));
        OPSHosts.add(new Host("ws1", Host.Type.workStationWindows, "198.18.3.11", "2a07:1181:130:3603::11", OPS, 1039, 479));
        OPSHosts.add(new Host("ws2", Host.Type.workStationWindows, "198.18.3.12", "2a07:1181:130:3603::12", OPS, 1181, 477));
        OPSHosts.add(new Host("ws3", Host.Type.workStationWindows, "198.18.3.13", "2a07:1181:130:3603::13", OPS, 1321, 477));
        OPSHosts.add(new Host("ws4", Host.Type.workStationWindows, "198.18.3.14", "2a07:1181:130:3603::14", OPS, 1465, 478));
        OPSHosts.add(new Host("ws5", Host.Type.workStationWindows, "198.18.3.15", "2a07:1181:130:3603::15", OPS, 756, 605));
        OPSHosts.add(new Host("ws6", Host.Type.workStationWindows, "198.18.3.16", "2a07:1181:130:3603::16", OPS, 896, 604));
        OPSHosts.add(new Host("pxe1", Host.Type.workStationWindows, "198.18.3.19", "2a07:1181:130:3603::19", OPS, 1049, 604));
        OPSHosts.add(new Host("pxe2", Host.Type.workStationWindows, "198.18.3.20", "2a07:1181:130:3603::20", OPS, 213, 46));
        // fuzzer.ops.clf.ex 1201, 602
        OPSHosts.add(new Host("helium", Host.Type.workStationWindows, "198.18.3.31", "2a07:1181:130:3603::31", OPS, 1473, 602));
        result.put(OPS, OPSHosts);


        // ROUTERS

        // admin.clf.ex 730,43
        // r1.clf.ex 708,254
        // drone.clf.ex 1008,236
        // fw.clf.ex 577,384
        // wifi.int.clf.ex 501,677
        // fw.ops.clf.ex 1310,775


        // Return
        return result;
    }

    public static void startup () throws Exception  {
        Map<Network, List<Host>> res = generateInfra();

        List<Host> existing = DAO.get().getList(Host.class) ;
        if (existing != null && existing.size() > 1) return ;

        DAO.get().executeUpdateOrDeleteQuery("delete from Host");
        DAO.get().executeUpdateOrDeleteQuery("delete from Network");

        for (Network network : res.keySet()) {
            List<Host> hosts = res.get(network);
            DAO.get().execute(new Insert(network));
            for (Host host : hosts)
                DAO.get().execute(new Insert(host));

        }
    }

    public static void main(String[] args) throws Exception {
        Map<Network, List<Host>> res = generateInfra();

        DAO.get().executeUpdateOrDeleteQuery("delete from Host");
        DAO.get().executeUpdateOrDeleteQuery("delete from Network");

        for (Network network : res.keySet()) {
            List<Host> hosts = res.get(network);
            DAO.get().execute(new Insert(network));
            for (Host host : hosts)
                DAO.get().execute(new Insert(host));

        }

        System.out.println("Infra generated and stored in database");
        System.exit(0);
    }
}
