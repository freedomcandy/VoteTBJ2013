package roy.thatsvoteing.tools;

import java.util.ArrayList;

public class Content {
	public static String baseURL = "http://awards.thatsmags.com/mobile/event/1";
	public static int whichScreen = 0;    //分辨率
	public static boolean isVote = false;// 是否投票
	public static String voteResult = "";// 投票后返回的结果
	public static String cid = "";// categoryID
	public static String vid = "";// voteID
	public static String categoryName = "";// 分类名称
	public static String userID = "";// 用户的ID
	public static int whichVote = 0;
	public static ArrayList<String> voteid = new ArrayList<String>();
	public static ArrayList<String> votePic = new ArrayList<String>();
	public static ArrayList<String> voteName = new ArrayList<String>();
	public static ArrayList<String> voteDescription = new ArrayList<String>();
}
