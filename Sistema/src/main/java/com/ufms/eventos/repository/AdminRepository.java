// não sei se precisa

package com.ufms.eventos.repository;

import java.util.HashSet;

import com.ufms.eventos.model.Admin;

public class AdminRepository {
    private HashSet<Admin> admins;

    public AdminRepository() {
        this.admins = new HashSet<Admin>();
    }

    public HashSet<Admin> getAdmins() {
        return new HashSet<Admin>(this.admins); //retorna uma cópia do conjunto de eventos
    }

    public boolean addAdmin(Admin admin) {
        return this.admins.add(admin);
    }

    public boolean removeAdmin(Admin admin) {
        return this.admins.remove(admin);
    }
    

}
