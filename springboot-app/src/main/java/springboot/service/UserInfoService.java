package springboot.service;

import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import springboot.exception.EntityNotFoundException;
import springboot.model.UserInfo;
import springboot.repository.UserInfoRepository;

@Service
public class UserInfoService {
	private final UserInfoRepository userInfoRepo;

	@Autowired
	public UserInfoService(UserInfoRepository userInfoRepo) {
		this.userInfoRepo = userInfoRepo;
	}

	public UserInfo addUserInfo(UserInfo userInfo) {
		userInfo.setCreateDate(new Date());
		return userInfoRepo.save(userInfo);
	}

	public UserInfo updateUserInfo(UserInfo userInfo) {
		return userInfoRepo.save(userInfo);
	}

	public List<UserInfo> getAllUserInfo() {
		return userInfoRepo.findAll();
	}

	public void deleteUserInfoById(Long id) {
		UserInfo entity = this.findUserInfoById(id);
		entity.setIsDeleted(true);
		userInfoRepo.save(entity);
//		userInfoRepo.deleteById(id);
	}

	public UserInfo findUserInfoById(Long id) {
		return userInfoRepo.findById(id)
				.orElseThrow(() -> new EntityNotFoundException("User by id " + id + "was not found!"));
	}
}
