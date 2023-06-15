package com.intelligent.bot.api.wx;

import cn.hutool.core.lang.UUID;
import com.intelligent.bot.base.result.B;
import com.intelligent.bot.service.wx.WxService;
import com.intelligent.bot.utils.sys.RedisUtil;
import lombok.extern.log4j.Log4j2;
import me.chanjar.weixin.common.api.WxConsts;
import me.chanjar.weixin.common.bean.result.WxMediaUploadResult;
import me.chanjar.weixin.common.error.WxErrorException;
import me.chanjar.weixin.mp.api.WxMpService;
import me.chanjar.weixin.mp.bean.material.WxMpMaterial;
import me.chanjar.weixin.mp.bean.material.WxMpMaterialUploadResult;
import me.chanjar.weixin.mp.bean.result.WxMpQrCodeTicket;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.io.File;


@RestController
@RequestMapping(value = "/wx",produces = "application/json; charset=UTF-8")
@Log4j2
public class WxApi {

    @Resource
    WxService wxService;

    @Resource
    private WxMpService wxMpService;

    @Resource
    RedisUtil redisUtil;


    @RequestMapping(value = "/callBack", method = RequestMethod.GET)
    public String token(String signature,String timestamp,String nonce,String echostr) {
        log.error("wxtoken校验");
        // 通过检验signature对请求进行校验，若校验成功则原样返回echostr，表示接入成功，否则接入失败
        if (signature != null && wxMpService.checkSignature(timestamp,nonce , signature)) {
            return echostr;
        }
        return null;
    }

    @RequestMapping(value = "/callBack", method = RequestMethod.POST)
    public String message(HttpServletRequest request) throws Exception {
        return wxService.callbackEvent(request);
    }

    /**
     * 上传永久素材
     *
     * @return
     * @throws WxErrorException
     */
    @RequestMapping(value = "uploadPermanent",method = RequestMethod.POST)
    public String uploadPermanent() throws WxErrorException {
        File file = new File("/Users/sim/Desktop/111.jpg");
        WxMpMaterial wxMpMaterial = new WxMpMaterial();
        wxMpMaterial.setFile(file);
        wxMpMaterial.setName("logo");
        WxMpMaterialUploadResult wxMpMaterialUploadResult = wxMpService.getMaterialService().materialFileUpload(WxConsts.MediaFileType.IMAGE, wxMpMaterial);
        redisUtil.setCacheObject("MediaId",wxMpMaterialUploadResult.getMediaId());
        return "上传永久素材成功：mediaId:" + wxMpMaterialUploadResult.getMediaId();
    }

    /**
     * 上传临时素材
     *
     * @return
     * @throws WxErrorException
     */
    @RequestMapping(value = "uploadTemp",method = RequestMethod.POST)
    public String uploadTemp() throws WxErrorException {
        File file = new File("/Users/sim/Desktop/erweima.0df50348.jpg");
        WxMediaUploadResult wxMediaUploadResult = wxMpService.getMaterialService().mediaUpload(WxConsts.MediaFileType.IMAGE, file);
        return "上传临时素材成功：mediaId:" + wxMediaUploadResult.getMediaId();
    }

    @RequestMapping(value = "getTicket",method = RequestMethod.POST)
    public B<String> getTicket() throws WxErrorException {
        String qrUuid = UUID.randomUUID().toString();
        WxMpQrCodeTicket wxMpQrCodeTicket =
                this.wxMpService.getQrcodeService().qrCodeCreateTmpTicket(qrUuid, 10 * 60);
        String qrCodeUrl =
                this.wxMpService.getQrcodeService().qrCodePictureUrl(wxMpQrCodeTicket.getTicket());
        return B.okBuild(qrCodeUrl);
    }

//    public static String cookie = "PSTM=1624844868; __yjs_duid=1_74b1582968ab35c79e27eb146f3e82ba1624848994326; BIDUPSID=8B8A117A1543D6FC9055DAA1457AAB86; BAIDUID=5AAD51998F683F9348404CA8E56D6041:SL=0:NR=10:FG=1; MCITY=-282%3A; BDUSS_BFESS=kFwd2hNaXdsNUFjY3JSTVQwaHdoblVFa01BVVp1V2tCTUZNRTctUHE2amFxMWhrRUFBQUFBJCQAAAAAAAAAAAEAAADM3tkhX19fX19fX1~AttHVAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAANoeMWTaHjFkN; Hm_lvt_01e907653ac089993ee83ed00ef9c2f3=1680416205,1681810792; H_WISE_SIDS=219946_219560_216841_213357_214802_219943_213043_204909_230288_242234_110085_236307_243880_244255_244730_240590_245412_243706_249060_247585_249909_250137_250581_250738_250890_251095_251120_251426_249893_252577_253007_234295_253481_253427_203519_253879_253517_254323_254341_254425_254472_179347_254262_254729_250606_248124_254749_253212_255288_255300_253693_255449_251133_255646_255783_253569_252129_254765_255938_255959_255906_253665_107311_256063_256095_256093_255681_248697_253152_256083_255804_253990_256120_256215_255660_256108_255474_254076_256296_256316_251059_256025_229154_255179_245042_253900_256222_256441_256501_254831_256530_250841_256196_256707_8000053_8000107_8000133_8000135_8000146_8000149_8000155_8000163_8000168_8000184_8000185; H_WISE_SIDS_BFESS=219946_219560_216841_213357_214802_219943_213043_204909_230288_242234_110085_236307_243880_244255_244730_240590_245412_243706_249060_247585_249909_250137_250581_250738_250890_251095_251120_251426_249893_252577_253007_234295_253481_253427_203519_253879_253517_254323_254341_254425_254472_179347_254262_254729_250606_248124_254749_253212_255288_255300_253693_255449_251133_255646_255783_253569_252129_254765_255938_255959_255906_253665_107311_256063_256095_256093_255681_248697_253152_256083_255804_253990_256120_256215_255660_256108_255474_254076_256296_256316_251059_256025_229154_255179_245042_253900_256222_256441_256501_254831_256530_250841_256196_256707_8000053_8000107_8000133_8000135_8000146_8000149_8000155_8000163_8000168_8000184_8000185; BDORZ=B490B5EBF6F3CD402E515D22BCDA1598; H_PS_PSSID=38515_36561_38529_38470_38468_37935_37709_26350_22160_38543; BA_HECTOR=05ala120000lag040g0g8h6u1i4peen1m; delPer=0; PSINO=2; ZFY=zlfyaZb:A0khZVUlOSCFpM7goFhxxsEgkHrVnpR4MN90:C; BAIDUID_BFESS=5AAD51998F683F9348404CA8E56D6041:SL=0:NR=10:FG=1; BDRCVFR[feWj1Vr5u3D]=I67x6TjHwwYf0; __bid_n=187409c6b344368420f78f; XFT=XZjlWjQ1sMJjtjB4O4coPyobxPtfXUFoiGbr3v7WOYM=; XFI=bed64e60-e663-11ed-b903-b9a1474733fe; XFCS=3008B7C65B86F5662BE1056DF62D6B799ADD81644CFB0CA700F34AB2E25B5D11; Hm_lpvt_01e907653ac089993ee83ed00ef9c2f3=1682755160; ab_sr=1.0.1_YTlhYzEyMjFlZWFlNWZlODNmZjI1NmY2MmFmOGQyMGY1MjZjNmRkOGM2OTkyYWRhMGVhOTM3MDliYzllZDMwYmY4YWY4YzU0Y2M1MTZjMDNjNThiNDUzODUwY2Y4NDBlZjJjNWY4YWM1ZGYwN2ZiMGFiYmM4OTk4NzExZDc4MmQ5ODQzMWFjNDcwYWVjYTZkMDUyYjdiYTYwM2ZjM2EyNmY0MDFkYWIzYTJkODgyYWFhMTBhZDI0MWY0MmU2NGQz";
//    public static void main(String[] args) throws InterruptedException {
//        String prompt = "你是谁";
//        HashMap<String,String> header = new HashMap<>();
//        header.put("Cookie",cookie);
//        header.put("Content-Type","application/json");
//        header.put("User-Agent","Mozilla/5.0 (Macintosh; Intel Mac OS X 10_15_7) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/112.0.0.0 Safari/537.36");
////        header.put("Sec-Fetch-User","?1");
//        header.put("Sec-Fetch-Mode","cors");
//        header.put("sec-ch-ua-mobile","?0");
//        header.put("Sec-Fetch-Site","same-origin");
//        header.put("Sec-Ch-Ua-Platform","macOS");
//        header.put("SSec-Ch-Ua-Mobile","?0");
//        header.put("Sec-Ch-Ua","\"Chromium\";v=\"112\", \"Google Chrome\";v=\"112\", \"Not:A-Brand\";v=\"99\"");
//        header.put("Origin","https://yiyan.baidu.com");
//        header.put("Referer","https://yiyan.baidu.com/");
//        header.put("Host","yiyan.baidu.com");
//        header.put("Accept-Language","zh-CN,zh;q=0.9");
//        header.put("Accept-Encoding","gzip, deflate, br");
//        getSession(prompt, header);
//
//    }
//
//    public static void getSession(String prompt,HashMap<String,String> header) throws InterruptedException {
//        JSONObject jsonObject = new JSONObject();
//        jsonObject.put("sessionName",prompt);
//        jsonObject.put("timestamp",System.currentTimeMillis());
//        jsonObject.put("deviceType","pc");
//        header.put("Acs-Token",getSign());
//        String sessionJson = HttpUtil.createPost("https://yiyan.baidu.com/eb/session/new")
//                .addHeaders(header)
//                .body(JSONObject.toJSONString(jsonObject))
//                .execute()
//                .body();
//        String sessionId = JSONObject.parseObject(sessionJson).getJSONObject("data").getString("sessionId");
//        jsonObject = new JSONObject();
//        jsonObject.put("text",prompt);
//        jsonObject.put("timestamp",System.currentTimeMillis());
//        jsonObject.put("deviceType","pc");
//        String checkJson = HttpUtil.createPost("https://yiyan.baidu.com/eb/chat/check")
//                .addHeaders(header)
//                .body(JSONObject.toJSONString(jsonObject))
//                .execute()
//                .body();
//        jsonObject = new JSONObject();
//        jsonObject.put("sessionId",sessionId);
//        jsonObject.put("text",prompt);
//        jsonObject.put("parentChatId",0);
//        jsonObject.put("deviceType","pc");
//        jsonObject.put("type","10");
//        jsonObject.put("timestamp",System.currentTimeMillis());
//        jsonObject.put("code",0);
//        jsonObject.put("msg","");
//        jsonObject.put("sign",getSign());
//        String newJson = HttpUtil.createPost("https://yiyan.baidu.com/eb/chat/new")
//                .addHeaders(header)
//                .body(JSONObject.toJSONString(jsonObject))
//                .execute()
//                .body();
//        JSONObject newJsonData = JSONObject.parseObject(newJson).getJSONObject("data");
//        String chatId = newJsonData.getJSONObject("chat").getString("id");
//        String parentChatId = newJsonData.getJSONObject("botChat").getString("parent");
//        jsonObject = new JSONObject();
//        jsonObject.put("chatId",chatId);
//        jsonObject.put("deviceType","pc");
//        jsonObject.put("parentChatId",parentChatId);
//        jsonObject.put("sentenceId",0);
//        jsonObject.put("sessionId",sessionId);
//        jsonObject.put("stop",0);
//        while (true){
//            jsonObject.put("sign",getSign());
//            jsonObject.put("timestamp",System.currentTimeMillis());
//            System.out.println("params:"+JSONObject.toJSONString(jsonObject));
//            String body = getContent(header, jsonObject);
//            if(body.contains("用户访问被限制")){
//                break;
//            }
//            JSONObject queryJson = JSONObject.parseObject(body);
//            System.out.println("queryJson："+queryJson);
//            JSONObject data = queryJson.getJSONObject("data");
//            int stop = data.getInteger("stop");
//            int isEnd = data.getInteger("is_end");
//            if(stop != 0 || isEnd != 0){
//                System.out.println(data.getString("content"));
//                break;
//            }
//            jsonObject.put("sentenceId",data.getString("sent_id"));
//        }
//    }
//
//    public static String getContent(HashMap<String,String> header,JSONObject params){
//        header.put("Connection","keep-alive");
//        header.put("Accept","*/*");
//        return HttpUtil.createPost("https://yiyan.baidu.com/eb/chat/query")
//                .addHeaders(header)
//                .body(JSONObject.toJSONString(params))
//                .execute()
//                .body();
//    }
//    public static String getSign(){
//        String signJson = HttpUtil.createGet("https://chatgpt-proxy.lss233.com/yiyan-api/acs")
//                .cookie(cookie)
//                .execute()
//                .body();
//       return JSONObject.parseObject(signJson).getString("acs");
//    }
}
