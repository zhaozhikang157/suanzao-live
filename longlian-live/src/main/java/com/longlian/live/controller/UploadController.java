package com.longlian.live.controller;

import com.huaxin.util.Utility;
import com.huaxin.util.constant.CecurityConst;
import com.huaxin.util.constant.RedisKey;
import com.huaxin.util.redis.RedisUtil;
import com.longlian.dto.ActResultDto;
import com.longlian.live.service.AppUserService;
import com.longlian.live.service.StoreFileService;
import com.longlian.live.util.CompressPicUtil;
import com.longlian.live.util.LonglianSsoUtil;
import com.longlian.live.util.PicUtil;
import com.longlian.live.util.StoreFileUtil;
import com.longlian.live.util.pic.BlurPicUtil;
import com.longlian.model.StoreFile;
import com.longlian.token.AppUserIdentity;
import com.longlian.type.OssBucket;
import com.longlian.type.ReturnMessageType;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import net.coobird.thumbnailator.Thumbnails;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.util.DigestUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;
import org.springframework.web.servlet.ModelAndView;

import javax.imageio.ImageIO;
import javax.servlet.http.HttpServletRequest;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/file")
public class UploadController {
    private static Logger log = LoggerFactory.getLogger(UploadController.class);

    @Value("${exclude_file:jsp,classes,js,css,html}")
    private String exclude_file;
    @Autowired
    AppUserService appUserService;
    @Autowired
    StoreFileService storeFileService;
    @Autowired
    CompressPicUtil compressPicUtil;
    @Autowired
    RedisUtil redisUtil;
    @Autowired
    PicUtil picUtil;
    @Autowired
    private StoreFileUtil storeFileUtilLonglian;
    @Autowired
    private StoreFileUtil storeFileUtil;
    private String[] exts;

    @Value("${videoBasePath:longlian_test}")
    private String videoBasePath;

    /**
     * 上传文件公共功能
     *
     * @param request
     * @return {"code":0,"data":[{"createTime":1460432349459,"id":10
     * ,"md5":"32b536b74f8d23e25a8ae3de8f276da3"
     * ,"name":"工作日志-20160325.xlsx"
     * ,"size":11106,"type":"xlsx"
     * ,"url":"/upload/2016/04/94820d5e8c744a0c84e87d42e31f6a25.xlsx"}]
     * ,"msg":"","success":true}
     * @throws Exception
     */
    @RequestMapping("upload")
    @ResponseBody
    @ApiOperation(value = "上传文件公共功能", httpMethod = "GET", notes = "上传文件公共功能")
    public ActResultDto upload(HttpServletRequest request,
                               @ApiParam(required =true, name = "类型", value = "类型") String mustFileType,
                               @ApiParam(required =true, name = "名称", value = "名称") String fileName,
                               Boolean isRealCompress,String photo) throws Exception {
        ActResultDto result = new ActResultDto();
        MultipartHttpServletRequest multipartRequest = null;
        try {
            multipartRequest = (MultipartHttpServletRequest) request;
        } catch (ClassCastException e) {
            e.printStackTrace();
            log.error("上传出错", e);
            return new ActResultDto(ReturnMessageType.UPLOAD_NO_FILE.getCode());
        }

        Map<String, MultipartFile> map = multipartRequest.getFileMap();
        //那个模块的
        String module = multipartRequest.getParameter("module");
        List<StoreFile> flist = new ArrayList();
        for (String fname : map.keySet()) {
                // 获得文件：
                log.info("------------------------------", fname);
                MultipartFile file = multipartRequest.getFile(fname);

                if (file == null) {
                    continue;
                }
                if (file != null && excludeFile(file.getOriginalFilename())) {
                log.info("不支持相关格式,文件名为：" + file.getOriginalFilename());
                return new ActResultDto(ReturnMessageType.UPLOAD_NOT_SUPPORT.getCode());
            }
            //必须是什么类型的文件 ,如果不是
            if (!StringUtils.isEmpty(mustFileType) && !isInFile(file.getOriginalFilename(), mustFileType)) {
                log.info("必须是相关格式,文件名为：" + file.getOriginalFilename());
                return new ActResultDto(ReturnMessageType.UPLOAD_NOT_SUPPORT.getCode());
            }
            byte[] bytes = file.getBytes();
            String name = file.getOriginalFilename();
            if (!Utility.isNullorEmpty(name)) {
                //转成小写的
                String caseName = name.toLowerCase();
                if (isImage(caseName)  && file.getSize() > 100 * 1024) {
                    log.info("上传的文件触发压缩方法：{}, 文件大小：{}", file.getOriginalFilename(), file.getSize());
                    if("1".equals(photo)){
                        bytes = compressPicUtil.compressPic(file,  getExt(caseName));
                    }
                }
            }
            StoreFile storeFile = new StoreFile(name, DigestUtils.md5DigestAsHex(bytes), file.getSize(), module);
            storeFile = storeFileService.doUploadFile(bytes, storeFile);
            //等于null时，继续
            log.info("------------------------------", storeFile);
            if (storeFile == null || storeFile.getUrl() == null || "null".equals(storeFile.getUrl())) {
                continue;
            }
            flist.add(storeFile);
        }

        if (flist.size() < 1) {
            return new ActResultDto(ReturnMessageType.UPLOAD_NO_FILE.getCode());
        }
        //如果只有一个文件的话
        if (flist.size() == 1) {
            result.setData(flist.get(0));
        } else {
            result.setData(flist);
        }
        return result;
    }
    public ActResultDto uploadPress(HttpServletRequest request,String mustFileType,String fileName,
                               Boolean isRealCompress,String photo) throws Exception {
        ActResultDto result = new ActResultDto();
        MultipartHttpServletRequest multipartRequest = null;
        try {
            multipartRequest = (MultipartHttpServletRequest) request;
        } catch (ClassCastException e) {
            e.printStackTrace();
            log.error("上传出错", e);
            return new ActResultDto(ReturnMessageType.UPLOAD_NO_FILE.getCode());
        }

        Map<String, MultipartFile> map = multipartRequest.getFileMap();
        //那个模块的
        String module = multipartRequest.getParameter("module");
        List<StoreFile> flist = new ArrayList();
        for (String fname : map.keySet()) {
            // 获得文件：
            log.info("------------------------------", fname);
            MultipartFile file = multipartRequest.getFile(fname);

            if (file == null) {
                continue;
            }
            if (file != null && excludeFile(file.getOriginalFilename())) {
                log.info("不支持相关格式,文件名为：" + file.getOriginalFilename());
                return new ActResultDto(ReturnMessageType.UPLOAD_NOT_SUPPORT.getCode());
            }
            //必须是什么类型的文件 ,如果不是
            if (!StringUtils.isEmpty(mustFileType) && !isInFile(file.getOriginalFilename(), mustFileType)) {
                log.info("必须是相关格式,文件名为：" + file.getOriginalFilename());
                return new ActResultDto(ReturnMessageType.UPLOAD_NOT_SUPPORT.getCode());
            }
            byte[] bytes = file.getBytes();
            InputStream input = null;
            String name = file.getOriginalFilename();
            input = file.getInputStream();
            if (!Utility.isNullorEmpty(name)) {
                //转成小写的
                String caseName = name.toLowerCase();
                BufferedImage bufImg = null;
                if(input != null) {
                    try {
                        // 把图片读入到内存中
                        bufImg = ImageIO.read(input);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                if (isImage(caseName)) {
                    long size = file.getSize();
                    System.out.println("图片大小="+size);
                    //float thumScale = getThumbScale(size);
                    bufImg = Thumbnails.of(bufImg).width(132).height(132).allowOverwrite(true).asBufferedImage();
                    caseName = caseName.substring(caseName.lastIndexOf(".")+1);
                    log.info("上传的文件触发压缩方法：{}, 文件大小：{}", file.getOriginalFilename(), file.getSize());
                }
                bytes = imageToBytes(bufImg,caseName);
            }
            StoreFile storeFile =  new StoreFile(name,  DigestUtils.md5DigestAsHex(bytes), file.getSize() , module);
            storeFile = storeFileService.doUploadFile(bytes, storeFile);
            //等于null时，继续
            log.info("------------------------------", storeFile);
            if (storeFile == null || storeFile.getUrl() == null || "null".equals(storeFile.getUrl())) {
                continue;
            }
            flist.add(storeFile);
        }

        if (flist.size() < 1) {
            return new ActResultDto(ReturnMessageType.UPLOAD_NO_FILE.getCode());
        }
        //如果只有一个文件的话
        if (flist.size() == 1) {
            result.setData(flist.get(0));
        } else {
            result.setData(flist);
        }
        return result;
    }
    public  String base64ToUrlPress(String base64 ) {
        BufferedImage bufImg = null;
        String upper = base64.toUpperCase();
        if (upper.startsWith("HTTP://") || upper.startsWith("HTTPS://")) {
            return base64;
        }

        try {
            int index = base64.indexOf(",") + 1;
            String info = base64.substring(0 ,index );
            String image = base64.substring(index);
            log.info(info);
            log.info(image);
            byte[] bytes = this.generateImage(image);
            String md5 = DigestUtils.md5DigestAsHex(bytes);
            StoreFile storeFile  = new  StoreFile();
            storeFile.setSize((long)bytes.length);
            storeFile.setCreateTime(new Date());
            storeFile.setModule("");
            String ext = getExt(info);
            storeFile.setName(md5 + "." + ext);
            storeFile.setMd5(md5);
            long size = bytes.length/1024;
            float thumScale = getThumbScale(size);
            InputStream input = new ByteArrayInputStream(bytes);
            if(input != null) {
                try {
                    // 把图片读入到内存中
                    bufImg = ImageIO.read(input);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            bufImg = Thumbnails.of(bufImg).scale(thumScale).allowOverwrite(true).asBufferedImage();
            bytes = imageToBytes(bufImg, ext);
            String url = storeFileUtil.saveFile(bytes , storeFile);
            return url;
        } catch (Exception e) {
            log.error("转图片报错:", e);
        }
        return "";
    }
    public float getThumbScale(long size){
        float thumScale = 1.0f;
        if(size > 5 * 1024){//5M
            thumScale = 0.1f;
        } else if(size > 4 * 1024 && size <= 5 * 1024) {
            thumScale = 0.15f;
        } else if(size > 3 * 1024 && size <= 4 * 1024) {
            thumScale = 0.2f;
        } else if(size > 2 * 1024 && size <= 3 * 1024) {
            thumScale = 0.25f;
        } else if(size > 1 * 1024 && size <= 2 * 1024) {
            thumScale = 0.25f;
        } else if(size > 0.5 * 1024 && size <= 1 * 1024) {
            thumScale = 0.3f;
        }
        return thumScale;
    }
    /**
     * @Description: 将base64编码字符串转换为图片
     * @Author:
     * @CreateTime:
     * @param imgStr base64编码字符串
     * @return
     */
    private byte[] generateImage(String imgStr) {
        try {
            byte[] b = Base64.decodeBase64(imgStr);

            for (int i = 0; i < b.length; ++i) {
                if (b[i] < 0) {
                    b[i] += 256;
                }
            }
            return b;
        } catch (Exception e) {
            log.error("转图片报错:", e);
        }
        return null;
    }

    public static byte[] imageToBytes(BufferedImage bImage, String format) {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        try {
            ImageIO.write(bImage, format, out);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return out.toByteArray();
    }
    public boolean isImage(String caseName) {
        if ((caseName.endsWith(".jpg")
                || caseName.endsWith(".png")
                || caseName.endsWith(".bmp")
                || caseName.endsWith(".gif")
                || caseName.endsWith(".jpeg"))) {
            return true;
        }
        return false;
    }

    /**
     * 手机端上传多张 批量上传
     *
     * @param request
     * @param mustFileType
     * @param fileName
     * @param isRealCompress
     * @return
     * @throws Exception
     */
    @RequestMapping("uploadMore")
    @ResponseBody
    @ApiOperation(value = "手机端上传多张 批量上传", httpMethod = "GET", notes = "手机端上传多张 批量上传")
    public ActResultDto uploadMore(HttpServletRequest request,
                                   @ApiParam(required =true, name = "类型", value = "类型") String mustFileType,
                                   @ApiParam(required =true, name = "名称", value = "名称") String fileName,
                                   Boolean isRealCompress) throws Exception {
        ActResultDto result = new ActResultDto();
        MultipartHttpServletRequest multipartRequest = null;
        try {
            multipartRequest = (MultipartHttpServletRequest) request;
        } catch (ClassCastException e) {
            e.printStackTrace();
            log.error("上传出错", e);
            return new ActResultDto(ReturnMessageType.UPLOAD_NO_FILE.getCode());
        }
        Map<String, MultipartFile> map = multipartRequest.getFileMap();
        //那个模块的
        String module = multipartRequest.getParameter("module");
        List<StoreFile> flist = new ArrayList();
        for (String fname : map.keySet()) {
            // 获得文件：
            log.info("------------------------------", fname);
            List<MultipartFile> files = multipartRequest.getFiles(fname);
            for (MultipartFile file : files) {
                //MultipartFile file = multipartRequest.getFile(fname);
                if (file == null) {
                    continue;
                }
                if (file != null && excludeFile(file.getOriginalFilename())) {
                    log.info("不支持相关格式,文件名为：" + file.getOriginalFilename());
                    return new ActResultDto(ReturnMessageType.UPLOAD_NOT_SUPPORT.getCode());
                }
                //必须是什么类型的文件 ,如果不是
                if (!StringUtils.isEmpty(mustFileType) && !isInFile(file.getOriginalFilename(), mustFileType)) {
                    log.info("必须是相关格式,文件名为：" + file.getOriginalFilename());
                    return new ActResultDto(ReturnMessageType.UPLOAD_NOT_SUPPORT.getCode());
                }
                byte[] bytes = file.getBytes();
                String name = file.getOriginalFilename();

//                String smallUrl = null;
//                if (!Utility.isNullorEmpty(name)) {
//                    //转成小写的
//                    String caseName = name.toLowerCase();
//                    if (isImage(caseName) && file.getSize() > 100 * 1024) {
//                        log.info("上传的文件触发压缩方法：{}, 文件大小：{}", file.getOriginalFilename(), file.getSize());
//                        bytes = compressPicUtil.compressPic(file, getExt(caseName));
//                    }
//
//                    if (isImage(caseName)) {
//                        log.info("上传的文件触发压缩方法：{}, 文件大小：{}", file.getOriginalFilename(), file.getSize());
//                        byte[] smallBytes = compressPicUtil.compressPic(file, getExt(caseName) , true);
//                        if (smallBytes != null && smallBytes.length > 0 ) {
//                            StoreFile smallFile = new StoreFile("small_" + name,  DigestUtils.md5DigestAsHex(smallBytes), (long)smallBytes.length , module);
//                            smallFile = storeFileService.doUploadFile(smallBytes, smallFile);
//                            smallUrl = smallFile.getUrl();
//                        }
//                    }
//                }
                StoreFile storeFile = new StoreFile(name,  DigestUtils.md5DigestAsHex(bytes), file.getSize() , module);
                storeFile = storeFileService.doUploadFile(bytes, storeFile);

                storeFile.setSmallUrl(storeFile.getUrl() + "?x-oss-process=style/catH");
                //等于null时，继续
                log.info("------------------------------", storeFile);
                if (storeFile == null || storeFile.getUrl() == null || "null".equals(storeFile.getUrl())) {
                    continue;
                }
                flist.add(storeFile);
            }
        }
        if (flist.size() < 1) {
            return new ActResultDto(ReturnMessageType.UPLOAD_NO_FILE.getCode());
        }
        result.setData(flist);
        return result;
    }

    public String getExt(String caseName) {
        String ext = "jpg";
        int lastIndex = caseName.lastIndexOf(".");
        if (lastIndex >= 0) {
            ext = caseName.substring(lastIndex + 1);
        }
        return  ext;
    }

    /**
     * 上传文件公共功能
     *
     * @param request
     * @return {"code":0,"data":[{"createTime":1460432349459,"id":10
     * ,"md5":"32b536b74f8d23e25a8ae3de8f276da3"
     * ,"name":"工作日志-20160325.xlsx"
     * ,"size":11106,"type":"xlsx"
     * ,"url":"/upload/2016/04/94820d5e8c744a0c84e87d42e31f6a25.xlsx"}]
     * ,"msg":"","success":true}
     * @throws Exception
     */
    @RequestMapping("videoUpload")
    @ResponseBody
    @ApiOperation(value = "上传文件公共功能", httpMethod = "GET", notes = "上传文件公共功能")
    public ActResultDto videoUpload(HttpServletRequest request, String fileName) throws Exception {
        ActResultDto result = new ActResultDto();
        MultipartHttpServletRequest multipartRequest = null;
        try {
            multipartRequest = (MultipartHttpServletRequest) request;
        } catch (ClassCastException e) {
            e.printStackTrace();
            log.error("上传出错", e);
            return new ActResultDto(ReturnMessageType.UPLOAD_NO_FILE.getCode());
        }

        Map<String, MultipartFile> map = multipartRequest.getFileMap();
        //那个模块的
        String module = multipartRequest.getParameter("module");
        List<StoreFile> flist = new ArrayList();
        for (String fname : map.keySet()) {
            // 获得文件：
            log.info("------------------------------", fname);
            MultipartFile file = multipartRequest.getFile(fname);
            long size = multipartRequest.getContentLengthLong();

            if (file == null) {
                continue;
            }
            if (file != null && excludeFile(file.getOriginalFilename())) {
                log.info("不支持相关格式,文件名为：" + file.getOriginalFilename());
                return new ActResultDto(ReturnMessageType.UPLOAD_NOT_SUPPORT.getCode());
            }
            byte[] bytes = file.getBytes();
            StoreFile storeFile = new StoreFile(file.getOriginalFilename(),  DigestUtils.md5DigestAsHex(bytes), file.getSize() , module);
            storeFile.setBucket(OssBucket.longlian_input.getName());
            storeFile.setBasePath(this.videoBasePath);
            storeFile = storeFileService.doUploadFile(bytes, storeFile);
            storeFile.setPlayUrl(LonglianSsoUtil.getSignUrl(storeFile.getUrl()));
            //等于null时，继续
            if (storeFile == null || storeFile.getUrl() == null || "null".equals(storeFile.getUrl())) {
                continue;
            }
            flist.add(storeFile);
        }

        if (flist.size() < 1) {
            return new ActResultDto(ReturnMessageType.UPLOAD_NO_FILE.getCode());
        }
        //如果只有一个文件的话
        if (flist.size() == 1) {
            result.setData(flist.get(0));
        } else {
            result.setData(flist);
        }
        return result;
    }



    @RequestMapping(value = "/page", method = RequestMethod.GET)
    public ModelAndView index() {
        return new ModelAndView("/func/msg/upload");
    }

    /**
     * 判断文件是否能上传
     *
     * @param fileName
     * @return
     */
    private boolean excludeFile(String fileName) {
        if (exts == null) {
            exts = exclude_file.split(",");
        }
        if (exts != null) {
            for (String ext : exts) {
                if (fileName.endsWith("." + ext)) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * 判断文件是否在指定类型的文件
     *
     * @param fileName
     * @return
     */
    private boolean isInFile(String fileName, String must) {
        String[] exts = must.split(",");
        if (exts != null) {
            for (String ext : exts) {
                if (fileName.endsWith("." + ext)) {
                    return true;
                }
            }
        }
        return false;
    }


    /**
     * 判断是否为阿里云存储，如果是返回true,否则返回false
     *
     * @param url
     * @return
     */
    public boolean isAliBaYun(String url) {
        if (url.indexOf("aliyuncs") != -1) {
            return true;
        }
        return false;
    }

    public String getFileName(String fileUrl) {
        return fileUrl.substring(fileUrl.lastIndexOf("/") + 1);
    }

    /**
     * 修改用户头像
     */
    /*@RequestMapping("avatar.user")
    @ResponseBody
    @ApiOperation(value = "修改用户头像", httpMethod = "GET", notes = "修改用户头像")
    public ActResultDto avatar(HttpServletRequest request,
                               @ApiParam(required =true, name = "类型", value = "类型") String mustFileType,
                               @ApiParam(required =true, name = "名称", value = "名称") String fileName) throws Exception {
        AppUserIdentity token = (AppUserIdentity) request.getAttribute(CecurityConst.REQUEST_USER_ATTR);
        ActResultDto ac = upload(request, mustFileType, fileName, true , "1");
        if (ac.getData() != null) {
            StoreFile file = (StoreFile) ac.getData();
            appUserService.updatePhone(token.getId(), file.getUrl());
            String logoKey = RedisKey.user_bg_logo_ids;
            List list = new ArrayList();
            list.add(token.getId());
            redisUtil.lpush(logoKey, JsonUtil.toJson(list));
            redisUtil.hdel(RedisKey.ll_user_info, String.valueOf(token.getId()));
            Map map = appUserService.getNameAndPhoto(token.getId());
            String photo = "";
            if(map != null) {
                photo = map.get("photo") != null?map.get("photo").toString():"";
            }
            ac.setExt(photo);
        }
        return ac;
    }*/
    @RequestMapping("avatar.user")
    @ResponseBody
    @ApiOperation(value = "修改用户头像", httpMethod = "POST", notes = "修改用户头像")
    public ActResultDto avatar(HttpServletRequest request,@RequestParam(required = false) String baseUrl,
                                @ApiParam(required =true, name = "类型", value = "类型") String mustFileType,
                                @ApiParam(required =true, name = "名称", value = "名称") String fileName) throws Exception {
        AppUserIdentity token = (AppUserIdentity) request.getAttribute(CecurityConst.REQUEST_USER_ATTR);
        ActResultDto ac = null;
        Long tokenId = token.getId();
        if(Utility.isNullorEmpty(baseUrl)){
            ac = uploadPress(request, mustFileType, fileName, true , "1");
            if (ac.getData() != null) {
                StoreFile file = (StoreFile) ac.getData();
                appUserService.updatePhone(token.getId(), file.getUrl());
                redisUtil.hdel(RedisKey.ll_user_info, String.valueOf(token.getId()));

                ac = new ActResultDto();
                Map map = appUserService.getNameAndPhoto(tokenId);
                String headPhoto = map.get("photo") != null ? map.get("photo").toString() : "";
                updateBlurPhotoUrl(headPhoto, tokenId);
                Map maps = appUserService.getNameAndPhoto(token.getId());
                maps.put("url",file.getUrl());
                ac.setData(maps);
            }
        } else {//h5访问
            String url = base64ToUrlPress(baseUrl);
            if (!Utility.isNullorEmpty(url)) {
                ac = new ActResultDto();
                appUserService.updatePhone(tokenId, url);
                redisUtil.hdel(RedisKey.ll_user_info, String.valueOf(tokenId));
                Map map = appUserService.getNameAndPhoto(tokenId);
                String headPhoto = map.get("photo") != null ? map.get("photo").toString() : "";
                updateBlurPhotoUrl(headPhoto, tokenId);
                Map maps = appUserService.getNameAndPhoto(tokenId);
                maps.put("url",url);
                ac.setData(maps);
            }
        }
        //清空redis
        String key=RedisKey.user_info_key+token.getId();
        if(redisUtil.exists(key)){
            redisUtil.del(key);
        }
        return ac;
    }

    /**
     * 高斯处理
     * @param headPhoto
     * @param id
     * @return
     */
    public String updateBlurPhotoUrl(String headPhoto,Long id){
        System.out.println("高斯模糊图片处理进入:"+headPhoto+",id="+id);
        String blurUrl = "";
        try {
            URL url = new URL(headPhoto);
            URLConnection urlConn = url.openConnection();
            InputStream input = urlConn.getInputStream();
            BufferedImage blurredImage = ImageIO.read(input);
            //保存处理后的图
            ByteArrayOutputStream bos = new ByteArrayOutputStream();// 存储图片文件byte数组
            //模糊处理图片
            blurredImage = BlurPicUtil.blur(blurredImage, 200);
            ImageIO.write(blurredImage, "jpg", bos);
            try {
                String fileName = "";
                StoreFile storeFile = null;
                blurredImage = Thumbnails.of(blurredImage).scale(0.5f).allowOverwrite(true).asBufferedImage();
                //阿里云文件
                if (headPhoto.indexOf("longlian-live") != -1) {
                    fileName = headPhoto.substring(headPhoto.lastIndexOf("/") + 1);//截取文件名
                    String prevName = fileName.substring(0, fileName.lastIndexOf("."));
                    String ext = StoreFileUtil.getExtensionName(fileName);
                    fileName = prevName + "_blur." + ext;
                    byte[] bytes = imageToBytes(blurredImage, ext);
                    storeFile = new StoreFile(fileName, DigestUtils.md5DigestAsHex(bytes), 0L, null);
                } else if (headPhoto.indexOf("thirdwx.qlogo.cn") != -1 || headPhoto.indexOf("wx.qlogo.cn") != -1) {//没有阿里云存储头像文件
                    fileName = id + "_blur.jpg";
                    byte[] bytes = imageToBytes(blurredImage, "jpg");
                    storeFile = new StoreFile(fileName, DigestUtils.md5DigestAsHex(bytes), 0L, null);
                }
                storeFile.setCreateTime(new Date());
                blurUrl = storeFileUtilLonglian.saveFile(bos.toByteArray(), storeFile, "1");
                appUserService.updateUserBlurPhoto(id, blurUrl);
            } catch (Exception e) {
                e.printStackTrace();
                log.error("高斯模糊图片处理异常:", e);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return blurUrl;
    }
    /**
     * base 64 路径转换
     *
     * @param
     * @return
     */
    @RequestMapping(value = "/baseUrlConvert", method = RequestMethod.POST)
    @ResponseBody
    @ApiOperation(value = "base 64 路径转换", httpMethod = "POST", notes = "base 64 路径转换")
    public ActResultDto baseUrlConvert(HttpServletRequest request,
                                       @ApiParam(required =true, name = "路径", value = "路径")  String baseUrl) throws Exception {
        ActResultDto result = new ActResultDto();
        try {
            String url = picUtil.base64ToUrl(baseUrl);
            result.setData(url);
        } catch (Exception e) {
            e.printStackTrace();
            log.error("base64图片转换异常");
        }
        return result;

    }

    public static void main(String args[]) {
        String caseName = "dafdsafdsafdsjpeg";
        String ext = "jpg";
        int lastIndex = caseName.lastIndexOf(".");
        if (lastIndex >= 0) {
            ext = caseName.substring(lastIndex + 1);
        }
        System.out.println(ext);
    }
}
