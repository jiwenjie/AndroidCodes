package jingya.com.base_class_module;

import java.util.List;

/**
 * @author Kuky
 *         <p>
 *         动态权限申请监听
 */
public interface PermissionListener {
    void onGranted();

    void onDenied(List<String> deniedPermissions);
}
