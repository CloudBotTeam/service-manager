package cn.cloudbot.servicemanager.controller;

import cn.cloudbot.servicemanager.service.rss.pojo.ServicePOJO;
import lombok.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Collection;

@RestController
public class ServiceController {
    @Autowired
    private ServicerMap servicerMap;

    @GetMapping("/services")
    public ListServiceResp listService() {
        ListServiceResp resp = new ListServiceResp();
        resp.setServ_list(servicerMap.listPojo());
        return resp;
    }

    @GetMapping("/services/{serviceID}")
    public ServicePOJO getService(@PathVariable("serviceID") String serviceID) {
        return servicerMap.getServiceByName(serviceID).toServicePojo();
    }

}

@Data
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
class ListServiceResp {
    private Collection<ServicePOJO> serv_list;
}