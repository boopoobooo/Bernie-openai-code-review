package cn.junbao.middleware.sdk.infrastructure.weixin.DTO;

import java.util.HashMap;
import java.util.Map;

public class TempleteMessageDTO {

    private String touser = "omDlA6zKW08YwGTp9ZCEBY6t8cOY";
    private String template_id = "I0GUueCOsgfCVrCfcKGzG26PxT1QCcLDNVklMVnqkMk";
    private String url = "https://weixin.qq.com";
    private Map<String, Map<String, String>> data = new HashMap<>();


    public TempleteMessageDTO(String touser, String template_id) {
        this.touser = touser;
        this.template_id = template_id;
    }

    public void put(String key, String value) {
        data.put(key, new HashMap<String, String>() {
            private static final long serialVersionUID = 7092338402387318563L;

            {
                put("value", value);
            }
        });
    }

    public static void put(Map<String,Map<String,String>> data ,String templateKey, String value ){
        data.put(templateKey, new HashMap<String, String>() {
            private static final long serialVersionUID = 7092338402387318563L;

            {
                put("value", value);
            }
        });
    }

    public enum TemplateKey{
        REPO_NAME("repo_name","项目名称"),
        BRANCH_NAME("branch_name","分支名称"),
        COMMIT_AUTHOR("commit_author","提交作者"),
        COMMIT_MESSAGE("commit_message","提交信息"),
        ;
        private String code;
        private String info;

        TemplateKey(String code, String info) {
            this.code = code;
            this.info = info;
        }

        public String getCode() {
            return code;
        }

        public String getInfo() {
            return info;
        }
    }

    public String getTouser() {
        return touser;
    }

    public void setTouser(String touser) {
        this.touser = touser;
    }

    public String getTemplate_id() {
        return template_id;
    }

    public void setTemplate_id(String template_id) {
        this.template_id = template_id;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public Map<String, Map<String, String>> getData() {
        return data;
    }

    public void setData(Map<String, Map<String, String>> data) {
        this.data = data;
    }

}
