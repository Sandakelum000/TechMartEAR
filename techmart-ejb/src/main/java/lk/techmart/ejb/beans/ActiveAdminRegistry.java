package lk.techmart.ejb.beans;

import jakarta.ejb.ConcurrencyManagement;
import jakarta.ejb.ConcurrencyManagementType;
import jakarta.ejb.Singleton;

import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

@Singleton
@ConcurrencyManagement(ConcurrencyManagementType.BEAN)
public class ActiveAdminRegistry {
    private final Set<Integer> activeAdminIds = ConcurrentHashMap.newKeySet();

    public boolean registerAdmin(Integer adminId) {
        return activeAdminIds.add(adminId);
    }

    public void removeAdmin(Integer adminId) {
        if (adminId != null) {
            activeAdminIds.remove(adminId);
        }
    }
}
