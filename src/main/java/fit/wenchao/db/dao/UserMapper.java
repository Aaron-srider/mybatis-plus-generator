package fit.wenchao.db.dao;
import fit.wenchao.db.dao.UserPO;
import org.apache.ibatis.annotations.Mapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
@Mapper
public interface UserMapper extends BaseMapper<UserPO> {
}
