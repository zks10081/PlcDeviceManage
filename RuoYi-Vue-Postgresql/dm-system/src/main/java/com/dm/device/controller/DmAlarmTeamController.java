package com.dm.device.controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import javax.servlet.http.HttpServletResponse;

import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.dm.common.annotation.Log;
import com.dm.common.constant.HttpStatus;
import com.dm.common.core.controller.BaseController;
import com.dm.common.core.domain.AjaxResult;
import com.dm.common.core.domain.entity.SysDictData;
import com.dm.common.core.page.TableDataInfo;
import com.dm.common.enums.BusinessType;
import com.dm.common.utils.DictUtils;
import com.dm.common.utils.poi.ExcelUtil;
import com.dm.device.domain.DmAlarmLink;
import com.dm.device.domain.DmAlarmTeam;
import com.dm.device.domain.DmDevice;
import com.dm.user.domain.DmUser;
import com.dm.device.domain.vo.DmAlarmTeamQueryCriteria;
import com.dm.device.service.IDmAlarmLinkService;
import com.dm.device.service.IDmAlarmTeamService;
import com.dm.user.service.IDmUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * 告警队伍配置Controller
 *
 * @author zmkts
 * @date 2026-11-10
 */
@RestController
@RequestMapping("/device/alarmTeam")
public class DmAlarmTeamController extends BaseController
{
    @Autowired
    private IDmAlarmTeamService dmAlarmTeamService;

    @Autowired
    private IDmAlarmLinkService dmAlarmLinkService;

    @Autowired
    private IDmUserService dmUserService;

    /**
     * 查询告警队伍列表（MP 分页）
     */
    @PreAuthorize("@ss.hasPermi('device:alarmTeam:list')")
    @GetMapping("/list")
    public TableDataInfo list(DmAlarmTeamQueryCriteria criteria,
                              @RequestParam(name = "pageNum", defaultValue = "1") Integer pageNum,
                              @RequestParam(name = "pageSize", defaultValue = "10") Integer pageSize)
    {
        IPage<DmAlarmTeam> result = dmAlarmTeamService.findAll(criteria, new Page<>(pageNum, pageSize));
        TableDataInfo rspData = new TableDataInfo(result.getRecords(), (int) result.getTotal());
        rspData.setCode(HttpStatus.SUCCESS);
        rspData.setMsg("查询成功");
        return rspData;
    }

    /**
     * 导出告警队伍列表
     */
    @PreAuthorize("@ss.hasPermi('device:alarmTeam:export')")
    @Log(title = "告警队伍", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response, DmAlarmTeamQueryCriteria criteria)
    {
        List<DmAlarmTeam> list = dmAlarmTeamService.findAll(criteria, new Page<>(1, Integer.MAX_VALUE)).getRecords();
        ExcelUtil<DmAlarmTeam> util = new ExcelUtil<DmAlarmTeam>(DmAlarmTeam.class);
        util.exportExcel(response, list, "告警队伍数据");
    }

    /**
     * 获取告警队伍详细信息
     */
    @PreAuthorize("@ss.hasPermi('device:alarmTeam:query')")
    @GetMapping(value = "/{id}")
    public AjaxResult getInfo(@PathVariable("id") Long id)
    {
        return success(dmAlarmTeamService.getById(id));
    }

    /**
     * 查询队伍已选用户（对应 dispatch dmAlarmUserFlag）
     */
    @PreAuthorize("@ss.hasPermi('device:alarmTeam:list')")
    @GetMapping(value = "/userFlag/{teamId}")
    public AjaxResult userFlag(@PathVariable("teamId") String teamId)
    {
        return success(dmAlarmTeamService.selectTeamUserId(teamId));
    }

    /**
     * 查询队伍设备勾选情况（对应 dispatch dmAlarmDeviceFlag）
     */
    @PreAuthorize("@ss.hasPermi('device:alarmTeam:list')")
    @GetMapping(value = "/deviceFlag/{teamId}")
    public AjaxResult deviceFlag(@PathVariable("teamId") String teamId)
    {
        List<Map<String, Object>> maps = dmAlarmTeamService.selectDeviceSum(teamId);
        Map<String, String> deviceType = selectDictValueToLabel("device_type");

        Map<String, List<Map<String, String>>> result = new HashMap<>();
        for (Map<String, Object> map : maps)
        {
            Map<String, String> item = new HashMap<String, String>();
            item.put("id", String.valueOf(map.get("id")));
            item.put("name", String.format("%s-%s", map.get("serial_no"), map.get("name")));
            item.put("count", String.valueOf(map.get("link")));
            String isChoose = String.valueOf(Long.parseLong(String.valueOf(map.get("link"))) > 0);
            item.put("isChoose", isChoose);
            item.put("isOldChoose", isChoose);

            String type = deviceType.get(String.valueOf(map.get("type")));
            type = (type == null || type.isEmpty()) ? "未分类" : type;
            if (result.containsKey(type))
            {
                result.get(type).add(item);
            }
            else
            {
                List<Map<String, String>> typeList = new ArrayList<>();
                typeList.add(item);
                result.put(type, typeList);
            }
        }
        return success(result);
    }

    /**
     * 查询用户下告警设备（dispatch deviceInfoByUser）
     */
    @PreAuthorize("@ss.hasPermi('device:alarmTeam:list')")
    @GetMapping(value = "/deviceInfoByUser")
    public AjaxResult deviceInfoByUser(@RequestParam("userId") String userId)
    {
        List<DmDevice> list = dmAlarmTeamService.selectAlarmDeviceByUser(userId);
        return success(list);
    }

    /**
     * 查询设备下告警用户（dispatch userInfoByDevice）
     */
    @PreAuthorize("@ss.hasPermi('device:alarmTeam:list')")
    @GetMapping(value = "/userInfoByDevice")
    public AjaxResult userInfoByDevice(@RequestParam("deviceId") String deviceId)
    {
        List<DmUser> list = dmAlarmTeamService.selectAlarmUserByDevice(deviceId);
        return success(list);
    }

    /**
     * 新增告警队伍（主表 + 设备/成员关联）
     */
    @PreAuthorize("@ss.hasPermi('device:alarmTeam:add')")
    @Log(title = "告警队伍", businessType = BusinessType.INSERT)
    @Transactional(rollbackFor = Exception.class)
    @PostMapping
    public AjaxResult add(@RequestBody JSONObject param)
    {
        DmAlarmTeam team = parseTeam(param);
        Map<String, List<Map<String, String>>> deviceChooes = parseDevice(param);
        List<String> userChooes = parseUser(param);

        int i = dmAlarmTeamService.dmAlarmTeamAdd(team);

        List<DmAlarmLink> addList = new ArrayList<>();
        for (String type : deviceChooes.keySet())
        {
            for (Map<String, String> map : deviceChooes.get(type))
            {
                if ("true".equals(map.get("isChoose")))
                {
                    addList.add(buildLink(team.getId(), map.get("id"), "device"));
                }
            }
        }
        for (String user : userChooes)
        {
            addList.add(buildLink(team.getId(), user, "user"));
        }
        if (!addList.isEmpty())
        {
            dmAlarmLinkService.saveBatch(addList);
        }
        return toAjax(i);
    }

    /**
     * 修改告警队伍（增量更新关联）
     */
    @PreAuthorize("@ss.hasPermi('device:alarmTeam:edit')")
    @Log(title = "告警队伍", businessType = BusinessType.UPDATE)
    @Transactional(rollbackFor = Exception.class)
    @PutMapping
    public AjaxResult edit(@RequestBody JSONObject param)
    {
        DmAlarmTeam team = parseTeam(param);
        Map<String, List<Map<String, String>>> deviceChooes = parseDevice(param);
        List<String> userChooes = parseUser(param);

        int i = dmAlarmTeamService.updateDmAlarmTeam(team);

        List<DmAlarmLink> addList = new ArrayList<>();
        List<Long> deleteDeviceIds = new ArrayList<>();
        for (String type : deviceChooes.keySet())
        {
            for (Map<String, String> map : deviceChooes.get(type))
            {
                String isChoose = map.get("isChoose");
                if (!Objects.equals(map.get("isOldChoose"), isChoose))
                {
                    if ("true".equals(isChoose))
                    {
                        addList.add(buildLink(team.getId(), map.get("id"), "device"));
                    }
                    else if ("false".equals(isChoose))
                    {
                        deleteDeviceIds.add(Long.valueOf(map.get("id")));
                    }
                }
            }
        }

        List<String> oldUser = dmAlarmTeamService.selectTeamUserId(String.valueOf(team.getId()));
        List<Long> deleteUserIds = new ArrayList<>();
        for (String user : oldUser)
        {
            if (!userChooes.contains(user))
            {
                deleteUserIds.add(Long.valueOf(user));
            }
        }
        for (String user : userChooes)
        {
            if (!oldUser.contains(user))
            {
                addList.add(buildLink(team.getId(), user, "user"));
            }
        }

        // 关联表 id 无默认值，删除前先判空，避免生成 IN () 非法 SQL
        if (!deleteDeviceIds.isEmpty())
        {
            dmAlarmLinkService.remove(new LambdaQueryWrapper<DmAlarmLink>()
                    .eq(DmAlarmLink::getTeamId, team.getId())
                    .eq(DmAlarmLink::getType, "device")
                    .in(DmAlarmLink::getLinkId, deleteDeviceIds));
        }
        if (!deleteUserIds.isEmpty())
        {
            dmAlarmLinkService.remove(new LambdaQueryWrapper<DmAlarmLink>()
                    .eq(DmAlarmLink::getTeamId, team.getId())
                    .eq(DmAlarmLink::getType, "user")
                    .in(DmAlarmLink::getLinkId, deleteUserIds));
        }
        if (!addList.isEmpty())
        {
            dmAlarmLinkService.saveBatch(addList);
        }
        return toAjax(i);
    }

    /**
     * 删除告警队伍
     */
    @PreAuthorize("@ss.hasPermi('device:alarmTeam:remove')")
    @Log(title = "告警队伍", businessType = BusinessType.DELETE)
    @DeleteMapping("/{id}")
    public AjaxResult remove(@PathVariable("id") Long id)
    {
        return toAjax(dmAlarmTeamService.deleteDmAlarmTeam(id));
    }

    /** 成员候选：座机 */
    @PreAuthorize("@ss.hasPermi('device:alarmTeam:list')")
    @GetMapping("/transfer/landline")
    public AjaxResult transferLandline()
    {
        return success(toTransferOptions(dmUserService.dmGroupTransfer()));
    }

    /** 成员候选：手机 */
    @PreAuthorize("@ss.hasPermi('device:alarmTeam:list')")
    @GetMapping("/transfer/phone")
    public AjaxResult transferPhone()
    {
        return success(toTransferOptions(dmUserService.dmGroupTransferPhone()));
    }

    /** 成员候选：广播 */
    @PreAuthorize("@ss.hasPermi('device:alarmTeam:list')")
    @GetMapping("/transfer/broadcast")
    public AjaxResult transferBroadcast()
    {
        return success(toTransferOptions(dmUserService.dmGroupTransferBroadcast()));
    }

    /** 成员候选：外机 */
    @PreAuthorize("@ss.hasPermi('device:alarmTeam:list')")
    @GetMapping("/transfer/outer")
    public AjaxResult transferOuter()
    {
        return success(toTransferOptions(dmUserService.dmGroupTransferOuterPhone()));
    }

    /** 负责人下拉候选 */
    @PreAuthorize("@ss.hasPermi('device:alarmTeam:list')")
    @GetMapping("/leaderOptions")
    public AjaxResult leaderOptions()
    {
        List<Map<String, String>> options = new ArrayList<>();
        for (DmUser user : dmUserService.listUsers())
        {
            Map<String, String> option = new HashMap<String, String>();
            option.put("value", user.getUsernum());
            option.put("label", (user.getUsername() == null || user.getUsername().isEmpty())
                    ? user.getUsernum() : user.getUsername() + "（" + user.getUsernum() + "）");
            options.add(option);
        }
        return success(options);
    }

    /** ---------- 私有辅助方法 ---------- */

    private DmAlarmTeam parseTeam(JSONObject param)
    {
        JSONObject node = param.getJSONObject("team");
        if (node == null)
        {
            return new DmAlarmTeam();
        }
        return node.toJavaObject(DmAlarmTeam.class);
    }

    private Map<String, List<Map<String, String>>> parseDevice(JSONObject param)
    {
        Map<String, List<Map<String, String>>> result = new HashMap<>();
        JSONObject node = param.getJSONObject("device");
        if (node == null)
        {
            return result;
        }
        for (String type : node.keySet())
        {
            JSONArray array = node.getJSONArray(type);
            List<Map<String, String>> list = new ArrayList<>();
            if (array != null)
            {
                for (int i = 0; i < array.size(); i++)
                {
                    JSONObject item = array.getJSONObject(i);
                    Map<String, String> map = new HashMap<String, String>();
                    if (item != null)
                    {
                        for (String key : item.keySet())
                        {
                            Object value = item.get(key);
                            map.put(key, value == null ? null : String.valueOf(value));
                        }
                    }
                    list.add(map);
                }
            }
            result.put(type, list);
        }
        return result;
    }

    private List<String> parseUser(JSONObject param)
    {
        List<String> result = new ArrayList<>();
        JSONArray array = param.getJSONArray("user");
        if (array == null)
        {
            return result;
        }
        for (int i = 0; i < array.size(); i++)
        {
            Object value = array.get(i);
            if (value != null)
            {
                result.add(String.valueOf(value));
            }
        }
        return result;
    }

    private DmAlarmLink buildLink(Long teamId, String linkId, String type)
    {
        DmAlarmLink link = new DmAlarmLink();
        link.setTeamId(teamId);
        link.setLinkId(Long.valueOf(linkId));
        link.setType(type);
        return link;
    }

    private List<Map<String, String>> toTransferOptions(List<String> users)
    {
        List<Map<String, String>> options = new ArrayList<>();
        if (users == null)
        {
            return options;
        }
        for (String user : users)
        {
            Map<String, String> option = new HashMap<String, String>();
            option.put("key", user);
            option.put("label", user);
            options.add(option);
        }
        return options;
    }

    /**
     * Dm 字典适配：从字典缓存读取 value -> label 映射
     */
    private Map<String, String> selectDictValueToLabel(String dictType)
    {
        Map<String, String> map = new HashMap<>();
        List<SysDictData> dictDatas = DictUtils.getDictCache(dictType);
        if (dictDatas != null)
        {
            for (SysDictData dictData : dictDatas)
            {
                map.put(dictData.getDictValue(), dictData.getDictLabel());
            }
        }
        return map;
    }
}
