package chairing.chairing.service.wheelchair;

import chairing.chairing.domain.wheelchair.Wheelchair;
import chairing.chairing.domain.wheelchair.WheelchairStatus;
import chairing.chairing.domain.wheelchair.WheelchairType;
import chairing.chairing.repository.wheelchair.WheelchairRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import chairing.chairing.domain.wheelchair.Location;

import java.util.List;
import java.util.Optional;

@Service
public class WheelchairService {

    @Autowired
    private WheelchairRepository wheelchairRepository;

    public List<Wheelchair> getAllWheelchairs() {
        return wheelchairRepository.findAll();
    }

    public void saveLocation(Long id,Location location){
        Wheelchair wheelchair = wheelchairRepository.findById(id).orElseThrow(()-> new IllegalArgumentException("없는 휠체어 입니다."));
        wheelchair.updateLocation(location);
        wheelchairRepository.save(wheelchair);
    }

    public long getAvailableAdultWheelchairCount() {
        return wheelchairRepository.countByTypeAndStatus(WheelchairType.ADULT, WheelchairStatus.AVAILABLE);
    }

    public long getAvailableChildWheelchairCount() {
        return wheelchairRepository.countByTypeAndStatus(WheelchairType.CHILD, WheelchairStatus.AVAILABLE);
    }

}