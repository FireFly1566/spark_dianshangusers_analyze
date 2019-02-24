import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

public class FastjsonTest {

    public static void main(String[] args) {
        String json = "[{'姓名':'张三','年龄':'25'},{'姓名':'李四','年龄':'19'}]";

        JSONArray jsonArray = JSONArray.parseArray(json);

        JSONObject jsonObject = jsonArray.getJSONObject(0);

        System.out.println(jsonObject.getString("姓名"));

    }

}



